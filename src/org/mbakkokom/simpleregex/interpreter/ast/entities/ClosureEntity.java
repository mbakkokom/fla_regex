package org.mbakkokom.simpleregex.interpreter.ast.entities;

public class ClosureEntity implements Entity {
    protected Entity entity;

    public ClosureEntity() {
        this(null);
    }

    public ClosureEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public EntityType type() {
        return EntityType.ENTITY_CLOSURE;
    }

    public int precedence() {
        return 1;
    }
}
