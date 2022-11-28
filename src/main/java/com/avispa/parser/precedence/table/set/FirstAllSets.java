package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public final class FirstAllSets extends SimplePrecedenceSets {

    public FirstAllSets(ContextFreeGrammar grammar) {
        super(grammar, "FIRST_ALL");
    }

    /**
     * Finds first token
     * @param rhsTokens
     * @return
     */
    @Override
    protected GenericToken findToken(List<GenericToken> rhsTokens) {
        return rhsTokens.get(0);
    }
}
