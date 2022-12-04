package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
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
     * Finds last symbol
     * @param rhsSymbols
     * @return
     */
    @Override
    protected Symbol findSymbol(List<Symbol> rhsSymbols) {
        return rhsSymbols.get(rhsSymbols.size() - 1);
    }
}
