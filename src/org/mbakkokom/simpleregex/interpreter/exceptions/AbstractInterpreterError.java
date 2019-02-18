package org.mbakkokom.simpleregex.interpreter.exceptions;

public abstract class AbstractInterpreterError extends RuntimeException {
    protected int index;

    public AbstractInterpreterError() {
        this("", -1);
    }

    public AbstractInterpreterError(int index) {
        this("", index);
    }

    public AbstractInterpreterError(String msg) {
        this(msg, -1);
    }

    public AbstractInterpreterError(String msg, int index) {
        super(msg);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
