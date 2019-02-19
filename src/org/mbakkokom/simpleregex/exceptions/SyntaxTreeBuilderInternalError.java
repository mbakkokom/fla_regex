package org.mbakkokom.simpleregex.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public class SyntaxTreeBuilderInternalError extends AbstractSyntaxTreeBuilderError {
    public SyntaxTreeBuilderInternalError(String message, Token token) {
        super(message, token);
    }
}
