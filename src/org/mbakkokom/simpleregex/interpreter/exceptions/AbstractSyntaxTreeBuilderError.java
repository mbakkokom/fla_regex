package org.mbakkokom.simpleregex.interpreter.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public abstract class AbstractSyntaxTreeBuilderError extends AbstractInterpreterError {
    protected Token token;

    public AbstractSyntaxTreeBuilderError(String msg, int index) {
        super(msg, index);
    }

    public AbstractSyntaxTreeBuilderError(String msg, Token token) {
        super(msg, (token == null) ? -1 : token.getIndex());
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
