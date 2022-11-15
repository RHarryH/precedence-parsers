package com.avispa.parser.precedence.grammar;

import java.util.List;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
public interface IGrammar {
    String getName();

    Set<Terminal> getTerminals();
    Set<NonTerminal> getNonTerminals();
    List<Production> getProductions();
    NonTerminal getStart();
}
