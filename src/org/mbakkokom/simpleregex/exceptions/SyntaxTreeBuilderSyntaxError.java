package org.mbakkokom.simpleregex.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public class SyntaxTreeBuilderSyntaxError extends AbstractSyntaxTreeBuilderError {
    public SyntaxTreeBuilderSyntaxError(String message, int index) {
        super(message, index);
    }

    public SyntaxTreeBuilderSyntaxError(String message, Token token) {
        super(message, token);
    }
}
