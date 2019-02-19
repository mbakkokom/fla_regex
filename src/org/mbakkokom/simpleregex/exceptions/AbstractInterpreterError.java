package org.mbakkokom.simpleregex.exceptions;

public abstract class AbstractInterpreterError extends RuntimeException {
    protected int index;

    public AbstractInterpreterError() {
        this("", -1);
    }

    public AbstractInterpreterError(int index) {
        this("", index);
    }

    public AbstractInterpreterError(String message) {
        this(message, -1);
    }

    public AbstractInterpreterError(String message, int index) {
        super(message);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
