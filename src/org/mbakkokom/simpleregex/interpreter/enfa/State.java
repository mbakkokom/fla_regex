package org.mbakkokom.simpleregex.interpreter.enfa;

public class State {
    public String name;
    public boolean isFinalState;

    private Transition[] transitions;
    private int transitionsLastIndex = 0;

    public State(int initialTransitionCapacity) {
        this(initialTransitionCapacity, "", false);
    }

    public State(int initialTransitionCapacity, String name, boolean isFinalState) {
        this.transitions = new Transition[initialTransitionCapacity];
        this.name = name;
        this.isFinalState = isFinalState;
    }

    public void addTransition(Transition t) {
        if (this.transitions.length <= this.transitionsLastIndex) {
            throw new IndexOutOfBoundsException("transition list is full");
        } else {
            this.transitions[this.transitionsLastIndex++] = t;
        }
    }

    public void setTransition(int index, Transition t) {
        this.transitions[index] = t;
    }

    public Transition getTransition(int index) {
        return this.transitions[index];
    }

    public Transition[] getTransitions() {
        Transition[] r = new Transition[this.transitionsLastIndex];
        System.arraycopy(this.transitions, 0, r, 0, this.transitionsLastIndex);
        return r;
    }
}
