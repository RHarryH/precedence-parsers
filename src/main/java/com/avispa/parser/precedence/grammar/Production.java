package com.avispa.parser.precedence.grammar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Production {
    private NonTerminal lhs;
    private List<Symbol> rhs;

    public static Production of(NonTerminal lhs, List<Symbol> rhs) {
        return new Production(lhs, rhs);
    }

    @Override
    public String toString() {
        return lhs + " -> " + rhs;
    }
}
