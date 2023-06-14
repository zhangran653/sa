import org.junit.Test;
import sootup.core.Project;
import sootup.core.Language;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
import sootup.core.views.View;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.bytecode.inputlocation.PathBasedAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootClassSource;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;
import sootup.java.sourcecode.inputlocation.JavaSourcePathAnalysisInputLocation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class Test1 {
    @Test
    public void test1() {
        String projectPath = System.getProperty("user.dir");
        Path pathToBinary = Paths.get(projectPath, "target", "classes");

        AnalysisInputLocation<JavaSootClass> inputLocation = new JavaClassPathAnalysisInputLocation(pathToBinary.toString());

        JavaLanguage language = new JavaLanguage(8);

        Project<JavaSootClass, JavaView> project = JavaProject.builder(language).addInputLocation(inputLocation).build();
        System.out.println(project);
        JavaView view = project.createFullView();
        ClassType classType =
                project.getIdentifierFactory().getClassType("edu.tsinghua.Main");
        SootClass<JavaSootClassSource> sootClass = view.getClass(classType).get();

        MethodSignature methodSignature =
                project.getIdentifierFactory().getMethodSignature(classType,
                        "main", // method name
                        "void", // return type
                        Collections.singletonList("java.lang.String[]")); // args
        Optional<? extends SootMethod> opt = view.getMethod(methodSignature);
        if (opt.isPresent()) {
            SootMethod method = opt.get();
            List<Stmt> cfg = method.getBody().getStmts();
            System.out.println(cfg);
        }

        Optional<? extends SootMethod> opt2 = sootClass.getMethod(methodSignature.getSubSignature());

        if (opt2.isPresent()) {
            SootMethod method2 = opt2.get();
            method2.getBody().getStmts();
            Body body = method2.getBody();
            System.out.println(body);
            //[args := @parameter0: java.lang.String[], i = staticinvoke <edu.tsinghua.Main: int foo()>(), $stack2 = <java.lang.System: java.io.PrintStream out>, virtualinvoke $stack2.<java.io.PrintStream: void println(java.lang.String)>("Hello World!"), $stack3 = <java.lang.System: java.io.PrintStream out>, virtualinvoke $stack3.<java.io.PrintStream: void println(int)>(i), return]
        }
    }

    @Test
    public void test2() {
        String projectPath = System.getProperty("user.dir");
        Path pathToSource = Paths.get(projectPath, "src", "main","java");

        AnalysisInputLocation<JavaSootClass> inputLocation =
                new JavaSourcePathAnalysisInputLocation(pathToSource.toString());
        JavaLanguage language = new JavaLanguage(8);
        Project<JavaSootClass, JavaView> project =
                JavaProject.builder(language).addInputLocation(inputLocation).build();


        System.out.println(project);
        JavaView view = project.createFullView();
        ClassType classType =
                project.getIdentifierFactory().getClassType("edu.tsinghua.Main");
        SootClass<JavaSootClassSource> sootClass = view.getClass(classType).get();

        MethodSignature methodSignature =
                project.getIdentifierFactory().getMethodSignature(classType,
                        "main", // method name
                        "void", // return type
                        Collections.singletonList("java.lang.String[]")); // args
        Optional<? extends SootMethod> opt = view.getMethod(methodSignature);
        if (opt.isPresent()) {
            SootMethod method = opt.get();
            List<Stmt> cfg = method.getBody().getStmts();
            System.out.println(cfg);
        }

        Optional<? extends SootMethod> opt2 = sootClass.getMethod(methodSignature.getSubSignature());

        if (opt2.isPresent()) {
            SootMethod method2 = opt2.get();
            method2.getBody().getStmts();
            List<Stmt> cfg = method2.getBody().getStmts();
            System.out.println(cfg);
        }
    }


}
