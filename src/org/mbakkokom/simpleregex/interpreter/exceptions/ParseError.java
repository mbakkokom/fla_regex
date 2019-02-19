package org.mbakkokom.simpleregex.interpreter.exceptions;

public class ParseError extends AbstractInterpreterError {
    public ParseError() {
        super("", -1);
    }

    public ParseError(String message) {
        super(message, -1);
    }

    public ParseError(String message, int index) {
        super(message, index);
    }
}
