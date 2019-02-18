package org.mbakkokom.simpleregex.interpreter.exceptions;

public class InvalidOperationError extends ParseError {
    public InvalidOperationError() {
        super(-1, "");
    }

    public InvalidOperationError(int index) {
        super(index, "");
    }

    public InvalidOperationError(int index, String msg) {
        super(index, msg);
    }
}
