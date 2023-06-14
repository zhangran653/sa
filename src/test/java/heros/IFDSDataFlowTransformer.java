package heros;

import java.io.File;
import java.util.*;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;
import soot.toolkits.scalar.Pair;

// Subclass of SceneTransformer to run Heros IFDS solver in Soot's "wjtp" pack
public class IFDSDataFlowTransformer extends SceneTransformer {
    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();
        IFDSTabulationProblem<Unit, Pair<Value,
                Set<DefinitionStmt>>, SootMethod,
                InterproceduralCFG<Unit, SootMethod>> problem = new IFDSReachingDefinitions(icfg);

        IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>,
                SootMethod, InterproceduralCFG<Unit, SootMethod>> solver =
                new IFDSSolver<>(problem);

        System.out.println("Starting solver");
        solver.solve();
        System.out.println("Done");
    }

    public static void main(String[] args) {
        // Set Soot's internal classpath
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        String rt = javaHome + File.separator + "lib" + File.separator + "rt.jar";
        String sootCp = userdir + File.separator + "target" + File.separator + "classes" + File.pathSeparator + rt;
        Options.v().set_process_dir(Collections.singletonList(userdir + File.separator + "target" + File.separator + "classes"));
        Options.v().set_soot_classpath(sootCp);

        // Enable whole-program mode
        Options.v().set_whole_program(true);
        Options.v().set_app(true);

        // Call-graph options
        Options.v().setPhaseOption("cg", "safe-newinstance:true");
        Options.v().setPhaseOption("cg.cha", "enabled:false");

        // Enable SPARK call-graph construction
        Options.v().setPhaseOption("cg.spark", "enabled:true");
        Options.v().setPhaseOption("cg.spark", "verbose:true");
        Options.v().setPhaseOption("cg.spark", "on-fly-cg:true");

        Options.v().set_allow_phantom_refs(true);
        String targetClass = "edu.tsinghua.Main";
        // Set the main class of the application to be analysed
        Options.v().set_main_class(targetClass);

        // Load the main class
        SootClass c = Scene.v().loadClass(targetClass, SootClass.BODIES);
        c.setApplicationClass();

        // Load the "main" method of the main class and set it as a Soot entry point
        SootMethod entryPoint = c.getMethodByName("main");
        List<SootMethod> entryPoints = new ArrayList<>();
        entryPoints.add(entryPoint);
        Scene.v().setEntryPoints(entryPoints);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new IFDSDataFlowTransformer()));

        String[] sootArgs = {"-w","-d", "./"};
        soot.Main.main(sootArgs);

    }
}
