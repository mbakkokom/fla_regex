package org.mbakkokom.simpleregex.interpreter.tokenizer;

public class Token {
    protected TokenType type;
    protected char rawValue;
    protected int index;

    public Token() {
        this(TokenType.TOKEN_INVALID, '\0', -1);
    }

    public Token(TokenType type, char rawValue, int index) {
        this.type = type;
        this.rawValue = rawValue;
        this.index = index;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public char getRawValue() {
        return rawValue;
    }

    public void setRawValue(char rawValue) {
        this.rawValue = rawValue;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
