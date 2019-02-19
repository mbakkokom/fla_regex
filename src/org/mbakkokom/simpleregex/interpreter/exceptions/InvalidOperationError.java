package org.mbakkokom.simpleregex.interpreter.exceptions;

public class InvalidOperationError extends ParseError {
    public InvalidOperationError() {
        super("", -1);
    }

    public InvalidOperationError(int index) {
        super("", -1);
    }

    public InvalidOperationError(int index, String message) {
        super(message, -1);
    }
}
