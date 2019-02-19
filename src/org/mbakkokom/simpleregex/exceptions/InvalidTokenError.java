package org.mbakkokom.simpleregex.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public class InvalidTokenError extends ParseError {
    private Token token;

    public InvalidTokenError(String message, Token token) {
        super(message, token.getIndex());
        this.token = token;
    }
}
