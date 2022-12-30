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

import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class FirstSets extends PrecedenceSets {
    public FirstSets(PrecedenceSets firstAll, Set<Terminal> terminals) {
        super("FIRST");
        log.debug("Constructing {} set.", name);
        initialize(terminals);
        construct(firstAll);
        log.debug("{}", this);
    }

    /**
     * For each terminal add self to the FIRST set
     * @param terminals
     */
    private void initialize(Set<Terminal> terminals) {
        for(Terminal terminal : terminals) {
            this.sets.put(terminal, Set.of(terminal));
        }
    }

    /**
     * Updates FIRST set by adding entries for non-terminals. FIRST for non-terminal is a FIRST_ALL for that non-terminal
     * with terminals only.
     */
    private void construct(PrecedenceSets firstAll) {
        var firstAllMap = firstAll.get();
        for(var firstAllForSymbol : firstAllMap.entrySet()) {
            Symbol setSymbol = firstAllForSymbol.getKey();
            if(NonTerminal.isOf(setSymbol)) {
                for(Symbol symbol : firstAllForSymbol.getValue()) {
                    if(Terminal.isOf(symbol)) {
                        update(setSymbol, symbol);
                    }
                }
            }
        }
    }

    @Override
    protected Symbol findSymbol(List<Symbol> rhsSymbols) {
        throw new UnsupportedOperationException("This method is not required as FIRST is derived from FIRST_ALL");
    }
}
