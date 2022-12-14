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
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class OperatorPrecedenceSets extends PrecedenceSets {
    OperatorPrecedenceSets(Grammar grammar, String setsName) {
        super(setsName);
        log.debug("Constructing {} set for '{}' grammar.", setsName, grammar.getName());
        construct(grammar);
        log.debug("{}", this);
    }

    /**
     * Iterate through all productions to find sets elements.
     *
     * @param grammar context free grammar for which sets should be built
     */
    private void construct(Grammar grammar) {
        // group productions by left-hand side non-terminals so alternatives can be processed in single external loop run
        Map<NonTerminal, List<Production>> productionsByLhs =
                groupProductionsByLhs(grammar);

        for(NonTerminal nonTerminal : grammar.getNonTerminals()) {
            constructFor(nonTerminal, productionsByLhs, new ArrayDeque<>());
        }
    }

    /**
     * Iterate through productions of provided left-hand side non-terminal to find its operator precedence set.
     *
     * @param lhs current lhs non-terminal
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     */
    private Set<Symbol> constructFor(NonTerminal lhs, Map<NonTerminal, List<Production>> productionsByLhs, Deque<NonTerminal> recursionChain) {
        log.debug("Started processing of '{}' non-terminal.", lhs);

        Set<Symbol> set = new HashSet<>();
        for (Production production : productionsByLhs.get(lhs)) { // for all alternatives
            set.addAll(constructDownstream(lhs, production.getRhs(), productionsByLhs, recursionChain));
        }
        return set;
    }

    /**
     * Method constructs set of terminals, which applies to FIRST_OP or LAST_OP sets defined for left-hand side non-terminal.
     * First it gets first/last symbol of production and it i
     * First/last terminals on the right are added to FIRST_OP and LAST_OP sets respectively. If symbol is a non-terminal
     * it is recursively checked for it's first/last symbols until end of possible derivation
     * is reached. The algorithm does not do recursive check if non-terminal was already visited to prevent infinite loop.
     *
     * @param lhs
     * @param rhsSymbols
     * @param productionsByLhs
     * @param recursionChain
     * @return
     */
    private Set<Symbol> constructDownstream(NonTerminal lhs, List<Symbol> rhsSymbols, Map<NonTerminal, List<Production>> productionsByLhs, Deque<NonTerminal> recursionChain) {
        Symbol symbol = findSymbol(rhsSymbols);

        Set<Symbol> downstreamTerminals = new HashSet<>();
        if(NonTerminal.isOf(symbol)) {
            NonTerminal nonTerminal = (NonTerminal) symbol;

            if(this.sets.containsKey(symbol)) {
                log.debug("Set for '{}' already exists. It will be reused.", symbol);
                downstreamTerminals = new HashSet<>(this.sets.get(symbol)); // make a copy, otherwise set for last symbol non-terminal will be overwritten
            } else if(recursionChain.contains(nonTerminal)) {
                log.debug("Set for '{}' does not exist and is already under construction.", nonTerminal);
            } else {
                log.debug("Set for '{}' does not exists. It will be created by recursive construction.", nonTerminal);

                recursionChain.push(nonTerminal);
                downstreamTerminals = constructFor(nonTerminal, productionsByLhs, recursionChain);
                recursionChain.pop();
            }

            Terminal terminal = findTerminal(rhsSymbols); // find first terminal
            if(null != terminal) {
                downstreamTerminals.add(terminal); // add found terminal to propagate it upstream
            }
        } else {
            downstreamTerminals.add(symbol);
        }

        log.debug("Generated set for '{}' non-terminal: {}", lhs, downstreamTerminals);
        update(lhs, downstreamTerminals);

        return downstreamTerminals;
    }

    /**
     * Find first or last terminal from right-hand side symbols
     * @param rhsSymbols
     * @return
     */
    protected abstract Terminal findTerminal(List<Symbol> rhsSymbols);
}
