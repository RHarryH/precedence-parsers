package com.avispa.parser.precedence.grammar;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
public abstract class Symbol {
    @Setter(AccessLevel.PROTECTED)
    private String name;

    protected Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
