package org.mbakkokom.simpleregex.enfa.graph;

import org.mbakkokom.simpleregex.interpreter.ast.entities.EntityType;
import org.mbakkokom.simpleregex.interpreter.ast.entities.SymbolEntity;

public class Transition {
    private SymbolEntity symbol;
    private State nextState;

    public Transition(SymbolEntity symbol) {
        this(symbol, null);
    }

    public Transition(char symbolChar, State nextState) {
        this(new SymbolEntity(symbolChar), nextState);
    }

    public Transition(SymbolEntity symbol, State nextState) {
        this.symbol = symbol;
        this.nextState = nextState;
    }

    public char getSymbolChar() {
        return this.symbol.getSymbolChar();
    }

    public boolean isSpecialSymbol() {
        return this.symbol.type() == EntityType.ENTITY_SPECIAL_SYMBOL;
    }

    public SymbolEntity getSymbol() {
        return symbol;
    }

    public boolean match(SymbolEntity symbol) {
        return this.symbol.equals(symbol);
    }

    public State getNextState() {
        return nextState;
    }
}
