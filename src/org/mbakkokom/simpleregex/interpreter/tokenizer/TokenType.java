package org.mbakkokom.simpleregex.interpreter.tokenizer;

public enum TokenType {
    TOKEN_INVALID,
    TOKEN_SYMBOL,
    TOKEN_SYMBOL_SPC_EMPTY_STRING,
    TOKEN_SYMBOL_SPC_EMPTY_SET,
    TOKEN_PAREN_OPEN,
    TOKEN_PAREN_CLOSE,
    TOKEN_OP_UNION,
    TOKEN_OP_CLOSURE
}
