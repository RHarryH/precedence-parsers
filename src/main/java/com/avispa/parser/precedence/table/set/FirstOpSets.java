package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public final class FirstOpSets extends OperatorPrecedenceSets {
    public FirstOpSets(Grammar grammar) {
        super(grammar, "FIRST_OP");
    }

    /**
     * Finds first terminal for right-hand side production symbols
     * @param rhsSymbols
     * @return
     */
    @Override
    protected Terminal findTerminal(List<Symbol> rhsSymbols) {
        log.debug("Looking for first terminal in {} right-hand side symbols.", rhsSymbols);
        for (Symbol symbol : rhsSymbols) {
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
        return rhsSymbols.get(0);
    }
}
