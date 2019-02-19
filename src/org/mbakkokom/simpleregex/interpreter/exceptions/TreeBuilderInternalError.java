package org.mbakkokom.simpleregex.interpreter.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public class TreeBuilderInternalError extends AbstractTreeBuilderError {
    public TreeBuilderInternalError(String message, Token token) {
        super(message, token);
    }
}
