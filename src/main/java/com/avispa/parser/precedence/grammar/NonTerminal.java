package com.avispa.parser.precedence.grammar;

import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode(callSuper = true)
public final class NonTerminal extends GenericToken {

    public static NonTerminal of(String name) {
        return new NonTerminal(name);
    }

    public static boolean isOf(GenericToken token) {
        return token instanceof NonTerminal;
    }

    private NonTerminal(String name) {
        super(name);
    }
}
