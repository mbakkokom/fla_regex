package org.mbakkokom.simpleregex.interpreter.exceptions;

import org.mbakkokom.simpleregex.interpreter.tokenizer.Token;

public class TreeBuilderSyntaxError extends AbstractTreeBuilderError {
    public TreeBuilderSyntaxError(String msg, int index) {
        super(msg, index);
    }

    public TreeBuilderSyntaxError(String msg, Token token) {
        super(msg, token);
    }
}
