import org.junit.Test;
import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class Test2 {
    @Test
    public void test1() {
        G.reset();
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        String sootCp =
                userdir
                        + File.separator
                        + "target"
                        + File.separator
                        + "classes"
                        + File.pathSeparator + javaHome + File.separator + "lib" + File.separator + "rt.jar";

        Options.v().set_soot_classpath(sootCp);
        Options.v().set_whole_program(true);
        Options.v().setPhaseOption("cg.cha", "on");
        Options.v().setPhaseOption("cg", "all-reachable:true");
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_prepend_classpath(false);

        Scene.v().addBasicClass("java.lang.StringBuilder");
        SootClass cs =
                Scene.v().forceResolve("edu.tsinghua.Main", SootClass.BODIES);
        if (cs != null) {
            cs.setApplicationClass();
        }
        Scene.v().loadNecessaryClasses();

        SootMethod method = null;
        for (SootClass c : Scene.v().getApplicationClasses()) {
            if (c.getName().equals("edu.tsinghua.Main")) {
                for (SootMethod m : c.getMethods()) {
                    if (m.getName().equals("main")) {
                        m.retrieveActiveBody();
                        method = m;
                        break;
                    }
                }
            }
        }

        UnitPatchingChain chain = method.getActiveBody().getUnits();
        System.out.println(chain);
        //[args := @parameter0: java.lang.String[], $stack2 = staticinvoke <edu.tsinghua.Main: int foo()>(), $stack3 = <java.lang.System: java.io.PrintStream out>, virtualinvoke $stack3.<java.io.PrintStream: void println(java.lang.String)>("Hello World!"), $stack4 = <java.lang.System: java.io.PrintStream out>, virtualinvoke $stack4.<java.io.PrintStream: void println(int)>($stack2), return]
    }

    @Test
    public void test2() {
        //Creating the Type Hierarchy
        String targetTestClassName = "edu.tsinghua.Main";
        G.reset();
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        String rt = javaHome + File.separator + "lib" + File.separator + "rt.jar";
        String sootCp = userdir + File.separator + "target" + File.separator + "classes" + File.pathSeparator + rt;
        Options.v().set_whole_program(true);
        Options.v().set_soot_classpath(sootCp);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().process_dir();
        Options.v().set_allow_phantom_refs(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_prepend_classpath(false);
        SootClass c = Scene.v().forceResolve(targetTestClassName, SootClass.BODIES);
        if (c != null)
            c.setApplicationClass();
        Scene.v().loadNecessaryClasses();

        //Defining an Entry Method
        //SootMethod src = Scene.v().getSootClass(targetTestClassName).getMethodByName("main");

        //Class Hierarchy Analysis
//        CHATransformer.v().transform();
//        CallGraph cg = Scene.v().getCallGraph();
//        Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(src));
//        while (targets.hasNext()) {
//            SootMethod tgt = (SootMethod) targets.next();
//            System.out.println(src + " may call " + tgt);
//        }


        //Rapid Type Analysis
//        Transform sparkConfig = new Transform("cg.spark", null);
//        PhaseOptions.v().setPhaseOption(sparkConfig, "enabled:true");
//        PhaseOptions.v().setPhaseOption(sparkConfig, "rta:true");
//        PhaseOptions.v().setPhaseOption(sparkConfig, "on-fly-cg:false");
//        Map<String, String> phaseOptions = PhaseOptions.v().getPhaseOptions(sparkConfig);
//        SparkTransformer.v().transform(sparkConfig.getPhaseName(), phaseOptions);
//        CallGraph cg2 = Scene.v().getCallGraph();
//        Iterator<MethodOrMethodContext> targets2 = new Targets(cg2.edgesOutOf(src));
//        while (targets2.hasNext()) {
//            SootMethod tgt = (SootMethod) targets2.next();
//            System.out.println(src + " may call " + tgt);
//        }


        Transform sparkConfig = new Transform("cg.spark", null);
        PhaseOptions.v().setPhaseOption(sparkConfig, "enabled:true");
        PhaseOptions.v().setPhaseOption(sparkConfig, "vta:true");
        PhaseOptions.v().setPhaseOption(sparkConfig, "on-fly-cg:false");
        Map<String,String> phaseOptions = PhaseOptions.v().getPhaseOptions(sparkConfig);
        SparkTransformer.v().transform(sparkConfig.getPhaseName(), phaseOptions);
        SootMethod src = Scene.v().getSootClass(targetTestClassName).getMethodByName("main");
        CallGraph cg = Scene.v().getCallGraph();
        Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(src));
        while (targets.hasNext()) {
            SootMethod tgt = (SootMethod)targets.next();
            System.out.println(src + " may call " + tgt);
        }
    }

}
