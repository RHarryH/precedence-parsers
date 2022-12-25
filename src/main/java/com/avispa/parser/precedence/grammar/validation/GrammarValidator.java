package com.avispa.parser.precedence.grammar.validation;

import com.avispa.parser.precedence.grammar.Grammar;

/**
 * @author Rafał Hiszpański
 */
@FunctionalInterface
public interface GrammarValidator {
    boolean is(Grammar grammar);
}
