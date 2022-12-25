package com.avispa.parser.precedence.grammar;

import java.util.List;
import java.util.Set;

/**
 * Formal grammar
 *
 * @author Rafał Hiszpański
 */
public interface Grammar {
    String getName();

    Set<Terminal> getTerminals();
    Set<NonTerminal> getNonTerminals();
    List<Production> getProductions();
    Production getProduction(int index);
    NonTerminal getStart();
}
