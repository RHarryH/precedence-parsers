package com.avispa.parser.precedence.grammar;

import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode(callSuper = true)
public final class NonTerminal extends Symbol {
    public static final NonTerminal START = NonTerminal.of("start");

    public static NonTerminal of(String name) {
        return new NonTerminal(name);
    }

    public static boolean isOf(Symbol symbol) {
        return symbol instanceof NonTerminal;
    }

    private NonTerminal(String name) {
        super(name);
    }
}
