package org.mbakkokom.simpleregex.interpreter.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public abstract class AbstractTreeBuilderError extends AbstractInterpreterError {
    protected Token token;

    public AbstractTreeBuilderError(String msg, int index) {
        super(msg, index);
    }

    public AbstractTreeBuilderError(String msg, Token token) {
        super(msg, (token == null) ? -1 : token.getIndex());
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
