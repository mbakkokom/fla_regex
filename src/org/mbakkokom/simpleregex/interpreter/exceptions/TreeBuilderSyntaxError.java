package org.mbakkokom.simpleregex.interpreter.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public class TreeBuilderSyntaxError extends AbstractTreeBuilderError {
    public TreeBuilderSyntaxError(String message, int index) {
        super(message, index);
    }

    public TreeBuilderSyntaxError(String message, Token token) {
        super(message, token);
    }
}
