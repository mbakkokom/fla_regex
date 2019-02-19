package org.mbakkokom.simpleregex.enfa.evaluator;

import org.mbakkokom.simpleregex.enfa.graph.Graph;
import org.mbakkokom.simpleregex.enfa.graph.State;
import org.mbakkokom.simpleregex.enfa.graph.Transition;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SpecialSymbolEntity;

import java.util.ArrayDeque;

public class Evaluator {
    private Graph graph;

    private Evaluator(Graph graph) {
        this.graph = graph;
    }

    public static Evaluator fromGraph(Graph graph) {
        return new Evaluator(graph);
    }

    private boolean _evaluate(EvaluatorString str) {
        ArrayDeque<EvaluatorState> stack = new ArrayDeque<>();
        int ln = str.value.size();

        stack.add(new EvaluatorState(str, 0, this.graph.beginState));

        while (!stack.isEmpty()) {
            EvaluatorState cur = stack.pop();
            State currentState = cur.currentState;

            int currentIndex = cur.currentIndex, nextIndex = currentIndex + 1;

            if (currentIndex == ln && currentState.isFinalState) {
                return true;
            }

            Transition[] transitions = currentState.getTransitions();
            for (Transition t : transitions) {
                State nextState = t.getNextState();

                if (t.getSymbol() == SpecialSymbolEntity.EmptyString) {
                    stack.addLast(new EvaluatorState(str, currentIndex, nextState));
                } if (currentIndex < ln && t.match(cur.getCurrentSymbolEntity())) {
                    stack.add(new EvaluatorState(str, nextIndex, nextState));
                }
            }
        }

        return false;
    }

    public boolean evaluate(String s) {
        if ((s == null || s == "") && this.graph == null) {
            return true;
        } else {
            EvaluatorString str = EvaluatorString.fromString(s);

            if (str.value.isEmpty()) {
                return this.graph == null;
            } else {
                return _evaluate(str);
            }
        }
    }
}
