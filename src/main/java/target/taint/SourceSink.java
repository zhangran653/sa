package target.taint;

import target.taint.internal.SinkClass;
import target.taint.internal.SourceClass;

public class SourceSink {

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        SinkClass sk = new SinkClass();
        sk.anInstanceSink(a);
    }
}
