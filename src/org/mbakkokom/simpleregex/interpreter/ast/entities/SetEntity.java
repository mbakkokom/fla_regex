package org.mbakkokom.simpleregex.interpreter.ast.entities;

import java.util.ArrayList;
import java.util.Arrays;

public class SetEntity extends Entity {
    protected ArrayList<Entity> dataSet;

    public SetEntity() {
        this.dataSet = new ArrayList<>();
    }

    public SetEntity(Entity[] nodes) {
        this.dataSet = new ArrayList<>(Arrays.asList(nodes));
    }

    public static SetEntity fromAppendSets(SetEntity s1, SetEntity s2) {
        SetEntity n = new SetEntity((Entity[]) s1.dataSet.toArray());
        n.addEntities((Entity[]) s2.dataSet.toArray());
        return n;
    }

    public void addEntity(Entity node) {
        this.dataSet.add(node);
    }

    public void addEntities(Entity[] nodes) {
        this.dataSet.addAll(Arrays.asList(nodes));
    }

    @Override
    public EntityType type() {
        return EntityType.ENTITY_SET;
    }
}
