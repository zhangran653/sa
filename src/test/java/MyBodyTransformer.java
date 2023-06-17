import soot.Body;
import soot.BodyTransformer;

import java.util.Map;

public class MyBodyTransformer extends BodyTransformer {
    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        System.out.println("Method Signature: " + body.getMethod().getSignature());
    }


}