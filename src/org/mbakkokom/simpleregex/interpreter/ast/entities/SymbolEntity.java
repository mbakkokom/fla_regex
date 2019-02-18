package org.mbakkokom.simpleregex.interpreter.ast.entities;

public class SymbolEntity extends Entity {
    protected char symbolChar;

    public SymbolEntity() {
        this((char) 0);
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

    @Override
    public EntityType type() {
        return EntityType.ENTITY_SYMBOL;
    }
}
