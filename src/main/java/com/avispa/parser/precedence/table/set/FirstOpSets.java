/*
 * MIT License
 *
 * Copyright (c) 2022 Rafał Hiszpański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
