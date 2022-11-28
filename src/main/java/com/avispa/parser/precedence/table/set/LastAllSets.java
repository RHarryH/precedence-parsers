package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class LastAllSets extends SimplePrecedenceSets {

    public LastAllSets(ContextFreeGrammar grammar) {
        super(grammar, "LAST_ALL");
    }

    /**
     * Finds last token
     * @param rhsTokens
     * @return
     */
    @Override
    protected GenericToken findToken(List<GenericToken> rhsTokens) {
        return rhsTokens.get(rhsTokens.size() - 1);
    }
}
