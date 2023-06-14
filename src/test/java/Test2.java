import org.junit.Test;
import soot.*;
import soot.options.Options;

import java.io.File;

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
}
