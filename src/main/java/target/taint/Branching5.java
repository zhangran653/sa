package target.taint;

import target.taint.internal.SourceClass;

public class Branching5 {

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        String b = a;
        Integer x = null;
        if(x!=null) {
            a = "";
        }
    }

}
