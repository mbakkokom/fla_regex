package org.mbakkokom.simpleregex.interpreter.ast.entities;

import org.mbakkokom.simpleregex.interpreter.exceptions.TreeBuilderInternalError;

import java.util.ArrayList;
import java.util.Arrays;

public class SetEntity extends ArrayList<Entity> implements Entity {
    public SetEntity() {
        super();
    }

    public SetEntity(Entity[] nodes) {
        super(Arrays.asList(nodes));
    }

    public static SetEntity fromAppendSets(SetEntity s1, SetEntity s2) {
        SetEntity n = new SetEntity((Entity[]) s1.toArray());
        n.addAll((Entity[]) s2.toArray());
        return n;
    }

    public void addAll(Entity[] nodes) {
        this.addAll(Arrays.asList(nodes));
    }

    public void replaceLastEntity(Entity replaceWith) {
        this.set(this.size() - 1, replaceWith);
    }

    public boolean replaceLastEntityOf(Entity target, Entity replaceWith) {
        int i;

        if ((i = this.lastIndexOf(target)) == -1) {
            return false;
        } else {
            this.set(i, replaceWith);
            return true;
        }
    }

    public EntityType type() {
        return EntityType.ENTITY_SET;
    }

    public int precedence() {
        return 1;
    }
}
