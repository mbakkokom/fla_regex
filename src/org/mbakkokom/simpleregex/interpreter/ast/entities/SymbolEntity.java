package org.mbakkokom.simpleregex.interpreter.ast.entities;

public class SymbolEntity implements Entity {
    protected char symbolChar;

    public SymbolEntity() {
        this((char) 0);
    }

    public SymbolEntity(SymbolEntity symbol) {
        this(symbol.getSymbolChar());
    }

    public SymbolEntity(char symbol) {
        this.symbolChar = symbol;
    }

    public char getSymbolChar() {
        return this.symbolChar;
    }

    public void setSymbolChar(char symbolChar) {
        this.symbolChar = symbolChar;
    }

    public EntityType type() {
        return EntityType.ENTITY_SYMBOL;
    }

    public int precedence() {
        return 0;
    }

    public boolean equals(SymbolEntity obj) {
        return this.type() == obj.type() && this.symbolChar == obj.symbolChar;
    }
}
