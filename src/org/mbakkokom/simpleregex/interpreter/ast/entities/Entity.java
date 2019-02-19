package org.mbakkokom.simpleregex.interpreter.ast.entities;

public interface Entity {
    EntityType type();
    int precedence();
}
