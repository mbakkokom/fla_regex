package org.mbakkokom.simpleregex.interpreter.exceptions;

public class ParseError extends AbstractInterpreterError {
    public ParseError() {
        super("", -1);
    }

    public ParseError(int index) {
        super("", index);
    }

    public ParseError(int index, String msg) {
        super(msg, index);
    }
}
