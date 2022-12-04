package com.avispa.parser.precedence.grammar;

import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
public abstract class Symbol {
    private final String name;

    Symbol(String name) {
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
