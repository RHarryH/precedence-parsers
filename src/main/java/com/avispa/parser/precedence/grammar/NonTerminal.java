package com.avispa.parser.precedence.grammar;

import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode(callSuper = true)
public class NonTerminal extends GenericToken {

    public static NonTerminal of(String name) {
        return new NonTerminal(name);
    }

    private NonTerminal(String name) {
        super(name);
    }
}
