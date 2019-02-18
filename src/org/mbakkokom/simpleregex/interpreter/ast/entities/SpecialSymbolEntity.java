package org.mbakkokom.simpleregex.interpreter.ast.entities;

public class SpecialSymbolEntity extends SymbolEntity {
    public static final SymbolEntity EmptyString = new SpecialSymbolEntity('ε');
    public static final SymbolEntity EmptySet = new SpecialSymbolEntity('∅');

    private SpecialSymbolEntity(char symbol) {
        super(symbol);
    }

    @Override
    public EntityType type() {
        return EntityType.ENTITY_SPECIAL_SYMBOL;
    }
}
