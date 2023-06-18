import soot.Unit;
import soot.Value;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.FlowSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleVeryBusyExpressions {
    private Map<Unit, List<Value>> unitToExpressionsAfter;
    private Map<Unit, List<Value>> unitToExpressionsBefore;

    public SimpleVeryBusyExpressions(DirectedGraph<Unit> graph) {
        VeryBusyExpressions analysis = new VeryBusyExpressions(graph);

        unitToExpressionsAfter = new HashMap<>(graph.size() * 2 + 1, 0.7f);
        unitToExpressionsBefore = new HashMap<>(graph.size() * 2 + 1, 0.7f);

        for (Unit s : graph) {

            FlowSet set = analysis.getFlowBefore(s);
            unitToExpressionsBefore.put(s,
                    Collections.unmodifiableList(set.toList()));

            set = analysis.getFlowAfter(s);
            unitToExpressionsAfter.put(s,
                    Collections.unmodifiableList(set.toList()));
        }
    }

    public List<Value> getBusyExpressionsAfter(Unit s) {
        return unitToExpressionsAfter.get(s);
    }

    public List<Value> getBusyExpressionsBefore(Unit s) {
        List<Value> foo = unitToExpressionsBefore.get(s);
        return foo;
    }
}
