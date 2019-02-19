package org.mbakkokom.simpleregex.enfa.graph;

import org.mbakkokom.simpleregex.interpreter.ast.entities.SymbolEntity;

import java.util.ArrayList;

public class Graph {
    public ArrayList<State> states = new ArrayList<>();
    public ArrayList<Transition> transitions = new ArrayList<>();

    public State beginState, endState;

    public State createState(int initialTransitionCount, String name) {
        State c = new State(initialTransitionCount, name, false);
        this.states.add(c);
        return c;
    }

    public State createState(int initialTransitionCount, String name, boolean isFinalState) {
        State c = new State(initialTransitionCount, name, isFinalState);
        this.states.add(c);
        return c;
    }

    public Transition createTransition(SymbolEntity symbol, State nextState) {
        Transition t = new Transition(symbol, nextState);
        this.transitions.add(t);
        return t;
    }
}
