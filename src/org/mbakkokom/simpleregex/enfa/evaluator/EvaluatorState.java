package org.mbakkokom.simpleregex.enfa.evaluator;

import org.mbakkokom.simpleregex.enfa.graph.State;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SpecialSymbolEntity;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SymbolEntity;

public class EvaluatorState {
    public EvaluatorString input;
    public int currentIndex;
    public State currentState;

    public EvaluatorState(EvaluatorString input, int currentIndex, State currentState) {
        this.input = input;
        this.currentIndex = currentIndex;
        this.currentState = currentState;
    }

    public SymbolEntity getCurrentSymbolEntity() {
        if (this.currentIndex < this.input.value.size()) {
            return this.input.getSymbolEntityAt(this.currentIndex);
        } else {
            return SpecialSymbolEntity.EmptyString;
        }
    }

    public boolean hasValidIndex() {
        return false;
    }
}
