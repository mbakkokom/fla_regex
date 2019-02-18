package org.mbakkokom.simpleregex.interpreter.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public class TreeBuilderUnexpectedTokenError extends AbstractTreeBuilderError {
    public TreeBuilderUnexpectedTokenError(String msg, int index) {
        super(msg, index);
    }

    public TreeBuilderUnexpectedTokenError(String msg, Token token) {
        super(msg, token);
    }
}
