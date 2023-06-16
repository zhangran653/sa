import org.junit.Test;
import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.io.File;
import java.util.*;

public class Test2 {
    @Test
    public void test1() {
        G.reset();
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        String sootCp = userdir + File.separator + "target" + File.separator + "classes" + File.pathSeparator + javaHome + File.separator + "lib" + File.separator + "rt.jar";

        //Options.v().set_soot_classpath(sootCp);
        Options.v().set_process_dir(Collections.singletonList(userdir + File.separator + "target" + File.separator + "classes"));//处理路径
        Options.v().set_whole_program(true);
        Options.v().setPhaseOption("cg.cha", "on");
        Options.v().setPhaseOption("cg", "all-reachable:true");
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_prepend_classpath(false);

        Scene.v().addBasicClass("java.lang.StringBuilder");
        SootClass cs = Scene.v().forceResolve("edu.tsinghua.Main", SootClass.BODIES);
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
        String targetTestClassName = "edu.tsinghua.A";
        G.reset();
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        String rt = javaHome + File.separator + "lib" + File.separator + "rt.jar";
        String sootCp = userdir + File.separator + "target" + File.separator + "classes" + File.pathSeparator + rt;
        Options.v().set_whole_program(true);
        //Options.v().set_soot_classpath(sootCp);
        Options.v().set_process_dir(Collections.singletonList(userdir + File.separator + "target" + File.separator + "classes"));//处理路径
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().process_dir();
        Options.v().set_allow_phantom_refs(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_prepend_classpath(false);
        SootClass c = Scene.v().forceResolve(targetTestClassName, SootClass.BODIES);
        if (c != null) c.setApplicationClass();
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
        Map<String, String> phaseOptions = PhaseOptions.v().getPhaseOptions(sparkConfig);
        SparkTransformer.v().transform(sparkConfig.getPhaseName(), phaseOptions);
        SootMethod src = Scene.v().getSootClass(targetTestClassName).getMethodByName("m1");


        CallGraph cg = Scene.v().getCallGraph();
        Set<SootMethod> visiteds = new HashSet<>();
        iterateCallGraph(src, visiteds, cg);


        src.retrieveActiveBody();
        Body activeBody = src.getActiveBody();

        UnitGraph g = new ExceptionalUnitGraph(activeBody);
        System.out.println(g);
        // Call the recursive function on each unit in the graph
        for (Unit unit : g) {
            Set<Unit> visited = new HashSet<>();
            iterateUnits(unit, visited, g);
        }
    }

    // Recursive function to iterate over the call graph
    void iterateCallGraph(SootMethod method, Set<SootMethod> visitedMethods, CallGraph cg) {
        if (visitedMethods.contains(method)) {
            return;  // Exit if the method has already been visited
        }

        visitedMethods.add(method);
        System.out.println("Method: " + method.toString());

        Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(method));
        while (targets.hasNext()) {
            SootMethod tgt = (SootMethod) targets.next();
            System.out.println("   may call " + tgt);
            iterateCallGraph(tgt, visitedMethods, cg);
        }

    }


    // Recursive function to iterate over nodes and their successors
    void iterateUnits(Unit unit, Set<Unit> visited, UnitGraph g) {
        if (visited.contains(unit)) {
            return;  // Exit if the unit has already been visited
        }

        visited.add(unit);
        System.out.println("Unit: " + unit.toString());
        System.out.println("Successors  :");
        // Get the successors of the current unit
        List<Unit> successors = g.getSuccsOf(unit);
        for (Unit succ : successors) {
            System.out.println("  " + succ.toString());
        }
        System.out.println();
        System.out.println();
        // Recursively iterate over the successors
        for (Unit succ : successors) {
            iterateUnits(succ, visited, g);
        }
    }


    @Test
    public void test3() {
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        String rt = javaHome + File.separator + "lib" + File.separator + "rt.jar";
        String classdir = userdir + File.separator + "target" + File.separator + "classes";

        soot.G.reset();//re-initializes all of soot
        Options.v().set_src_prec(Options.src_prec_class);//设置处理文件的类型,当然默认也是class文件
        Options.v().set_process_dir(Collections.singletonList(classdir));//处理路径
        Options.v().set_whole_program(true);//开启全局模式
        Options.v().set_prepend_classpath(true);//对应命令行的 -pp
        Options.v().set_output_format(Options.output_format_jimple);//输出jimple文件
        Options.v().set_allow_phantom_refs(true);
        Scene.v().loadNecessaryClasses();//加载所有需要的类

        SootMethod method = Scene.v().getSootClass("edu.tsinghua.Main").getMethodByName("main");
        method.retrieveActiveBody();
        Body activeBody = method.getActiveBody();
        UnitPatchingChain units = activeBody.getUnits();
        units.forEach(unit -> {
            //A statement in Soot is represented by the interface Unit, of which there are
            //different implementations for different IRs — e.g., Jimple uses Stmt while Grimp
            //uses Inst.
            System.out.print("unit: ");
            System.out.println(unit);

            //Through a Unit we can retrieve values used (getUseBoxes()), values defined
            //(getDefBoxes()) or even both (getUseAndDefBoxes()).

            System.out.print("unit.getUseBoxes(): ");
            System.out.println(unit.getUseBoxes());
            System.out.print("unit.getDefBoxes(): ");
            System.out.println(unit.getDefBoxes());

            // the units jumping to this unit (getBoxesPointingToThis()) and units this unit
            //is jumping to (getUnitBoxes())
            System.out.print("unit.getBoxesPointingToThis(): ");
            System.out.println(unit.getBoxesPointingToThis());
            System.out.print("unit.getUnitBoxes(): ");
            System.out.println(unit.getUnitBoxes());
            System.out.println();
        });

        //PackManager.v().runPacks();//运行
        PackManager.v().writeOutput();//输出jimple到sootOutput目录中
    }

    @Test
    public void test4() {
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        String rt = javaHome + File.separator + "lib" + File.separator + "rt.jar";
        String classdir = userdir + File.separator + "target" + File.separator + "classes";

        soot.G.reset();//re-initializes all of soot
        Options.v().set_src_prec(Options.src_prec_class);//设置处理文件的类型,当然默认也是class文件
        Options.v().set_process_dir(Collections.singletonList(classdir));//处理路径
        Options.v().set_whole_program(true);//开启全局模式
        Options.v().set_prepend_classpath(true);//对应命令行的 -pp
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_jimple);//输出jimple文件
        //Scene.v().loadNecessaryClasses();
        SootClass sClass = Scene.v().loadClassAndSupport("edu.tsinghua.Main");
        sClass.setApplicationClass();
        Scene.v().loadNecessaryClasses();

        for (SootMethod m : sClass.getMethods()) {
            Body b = m.retrieveActiveBody();

            System.out.println("=======================================");
            System.out.println(m);

            UnitGraph graph = new ExceptionalUnitGraph(b);
            SimpleVeryBusyExpressions vbe = new SimpleVeryBusyExpressions(graph);

            for (Unit u : graph) {
                List<Value> before = vbe.getBusyExpressionsBefore(u);
                List<Value> after = vbe.getBusyExpressionsAfter(u);
                UnitPrinter up = new NormalUnitPrinter(b);
                up.setIndent("");

                System.out.println("---------------------------------------");
                u.toString(up);
                System.out.println(up.output());
                System.out.print("Busy in: {");
                String sep = "";
                for (Value e : before) {
                    System.out.print(sep);
                    System.out.print(e.toString());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.print("Busy out: {");
                sep = "";
                for (Value e : after) {
                    System.out.print(sep);
                    System.out.print(e.toString());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.println("---------------------------------------");
            }

            System.out.println("=======================================");
        }
    }


    @Test
    public void test5() {
        String userdir = System.getProperty("user.dir");
        String classdir = userdir + File.separator + "target" + File.separator + "classes";
        String javaHome = System.getProperty("java.home");
        String lib = "/home/ran/.m2/repository/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar";
        String sootCp = lib;


        soot.G.reset();//re-initializes all of soot
        //Options.v().set_src_prec(Options.src_prec_class);//设置处理文件的类型,当然默认也是class文件
        Options.v().set_soot_classpath(sootCp);
        Options.v().set_process_dir(Collections.singletonList(classdir));//处理路径
        Options.v().set_whole_program(true);//开启全局模式-w: Enables whole-program mode and automatically adds necessary Soot transformations for the analysis.
        Options.v().set_prepend_classpath(true);//对应命令行的 -pp: Enables the whole-program mode, which performs interprocedural analysis on the entire program.
        Options.v().set_output_format(Options.output_format_jimple);//输出jimple文件
        Options.v().set_allow_phantom_refs(true);

        SootClass sClass = Scene.v().loadClassAndSupport("edu.tsinghua.A");
        sClass.setApplicationClass();
        Scene.v().loadNecessaryClasses();


        SootMethod method = sClass.getMethodByName("m1");
        method.retrieveActiveBody();
        Body activeBody = method.getActiveBody();

        UnitGraph g = new ExceptionalUnitGraph(activeBody);
        System.out.println(g);

        CHATransformer.v().transform();
        CallGraph cg = Scene.v().getCallGraph();
        //System.out.println(cg);
        for (Edge edge : cg) {
            SootMethod srcMethod = edge.src();
            SootMethod tgtMethod = edge.tgt();
            if (srcMethod.getSignature().startsWith("<edu.tsinghua.A") || srcMethod.getSignature().startsWith("<com.google.gson")) {
                // Print the source and target methods
                System.out.println("Source Method: " + srcMethod);
                System.out.println("Target Method: " + tgtMethod);
                System.out.println();
            }

        }
    }


    @Test
    public void test6() {
        String userdir = System.getProperty("user.dir");
        String classdir = userdir + File.separator + "target" + File.separator + "classes";

        String javaHome = System.getProperty("java.home");
        String lib = "/home/ran/.m2/repository/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar";
        String sootCp = lib;


        // Set Soot's internal classpath and main class
        String mainClass = "edu.tsinghua.A";
        Options.v().set_soot_classpath(sootCp);
        Options.v().set_process_dir(Collections.singletonList(classdir));
        //Options.v().set_main_class(mainClass);

        // Enable whole-program mode
        Options.v().set_whole_program(true);
        Options.v().setPhaseOption("cg", "safe-newinstance:true");
        Options.v().setPhaseOption("cg.cha", "enabled:false");
        Options.v().setPhaseOption("cg.spark", "enabled:true");
        Options.v().set_prepend_classpath(true);//对应命令行的 -pp: Enables the whole-program mode, which performs interprocedural analysis on the entire program.
        Options.v().set_allow_phantom_refs(true);

        Scene.v().loadNecessaryClasses();

        // Load the classes and build the call graph
        PackManager.v().runPacks();

        // Retrieve the call graph
        CallGraph callGraph = Scene.v().getCallGraph();

        // Iterate over the call graph edges
        for (Edge edge : callGraph) {
            SootMethod srcMethod = edge.src();
            SootMethod tgtMethod = edge.tgt();
            if (srcMethod.getSignature().startsWith("<edu.tsinghua")) {
                // Print the source and target methods
                System.out.println("Source Method: " + srcMethod);
                System.out.println("Target Method: " + tgtMethod);
                System.out.println();
            }

        }
    }
}
