package com.avispa.parser.precedence.grammar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Production {
    private final NonTerminal lhs;
    private final List<Symbol> rhs;

    public static Production of(NonTerminal lhs, List<Symbol> rhs) {
        return new Production(lhs, rhs);
    }

    public List<Symbol> getRhs() {
        return Collections.unmodifiableList(rhs);
    }

    @Override
    public String toString() {
        return lhs + " -> " + rhs;
    }
}
