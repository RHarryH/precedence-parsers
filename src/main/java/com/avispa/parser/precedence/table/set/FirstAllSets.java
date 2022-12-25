package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public final class FirstAllSets extends SimplePrecedenceSets {

    public FirstAllSets(Grammar grammar) {
        super(grammar, "FIRST_ALL");
    }

    /**
     * Finds first symbol
     * @param rhsSymbols
     * @return
     */
    @Override
    protected Symbol findSymbol(List<Symbol> rhsSymbols) {
        return rhsSymbols.get(0);
    }
}
