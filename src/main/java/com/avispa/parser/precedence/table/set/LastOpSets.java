package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public final class LastOpSets extends OperatorPrecedenceSets {
    public LastOpSets(ContextFreeGrammar grammar) {
        super(grammar, "LAST_OP");
    }

    /**
     * Finds last terminal for right-hand side production symbols
     * @param rhsSymbols
     * @return
     */
    @Override
    protected Terminal findTerminal(List<Symbol> rhsSymbols) {
        log.debug("Looking for last terminal in {} right-hand side symbols.", rhsSymbols);
        var it = rhsSymbols.listIterator(rhsSymbols.size());
        while(it.hasPrevious()) {
            Symbol symbol = it.previous();
            if (Terminal.isOf(symbol)) {
                log.debug("'{}' found.", symbol);
                return (Terminal) symbol;
            }
        }

        return null;
    }

    /**
     * Finds first symbol for right-hand side production symbols
     * @param rhsSymbols
     * @return
     */
    @Override
    protected Symbol findSymbol(List<Symbol> rhsSymbols) {
        return rhsSymbols.get(rhsSymbols.size() - 1);
    }
}
