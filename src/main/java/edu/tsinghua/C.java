package edu.tsinghua;

public class C {

    public int c1() {
        return 1 + c3();
    }

    public int c2() {
        return 0;
    }

    public int c3() {
        return 2;
    }

    public static void main(String[] args) {
        new B().b1();
    }
}
