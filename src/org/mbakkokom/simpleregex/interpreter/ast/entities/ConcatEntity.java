package org.mbakkokom.simpleregex.interpreter.ast.entities;

public class ConcatEntity implements Entity {
    protected Entity lValue, rValue;

    public ConcatEntity() {
        this(null, null);
    }

    public ConcatEntity(Entity lval, Entity rval) {
        this.lValue = lval;
        this.rValue = rval;
    }

    public Entity getlValue() {
        return lValue;
    }

    public void setlValue(Entity lValue) {
        this.lValue = lValue;
    }

    public Entity getrValue() {
        return rValue;
    }

    public void setrValue(Entity rValue) {
        this.rValue = rValue;
    }

    public EntityType type() {
        return EntityType.ENTITY_CONCAT;
    }

    public int precedence() {
        return 2;
    }
}
