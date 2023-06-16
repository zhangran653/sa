package edu.tsinghua;

import com.google.gson.Gson;

public class A {
    public void m1() {
        Gson gson = new Gson();
        String s = "hello";
        String a = foo(s);
        int x = 3;
        String b = a + x;
        if (b.length() > 3) {
            x = bar(x);
        }
        gson.toJson(new Main().foo() + x);
    }

    public String foo(String s) {
        return s + "world";
    }

    public int bar(int x) {
        return x + 1;
    }
}
