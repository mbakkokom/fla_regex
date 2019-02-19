package org.mbakkokom.simpleregex.interpreter.ast.entities;

public class SpecialSymbolEntity extends SymbolEntity {
    public static final SymbolEntity EmptyString = new SpecialSymbolEntity('ε');
    public static final SymbolEntity EmptySet = new SpecialSymbolEntity('∅');

    public SpecialSymbolEntity(SymbolEntity symbol) {
        this(symbol.getSymbolChar());
    }

    private SpecialSymbolEntity(char symbol) {
        super(symbol);
    }

    public EntityType type() {
        return EntityType.ENTITY_SPECIAL_SYMBOL;
    }
}
