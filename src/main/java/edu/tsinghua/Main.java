package edu.tsinghua;

public class Main {
    public int foo() {
        return 1;
    }

    public int bar(int i) {
        return i + 1;
    }

    public String zoo(){
        return "a";
    }


    public static void main(String[] args) {
        Main main = new Main();
        int x = 1;
        x = 2;
        int i = main.foo();
        int a = i + x;
        int b = main.bar(a);
        if (b > 1) {
            System.out.println(i + x);
        }
        new A().m1();

    }
}
