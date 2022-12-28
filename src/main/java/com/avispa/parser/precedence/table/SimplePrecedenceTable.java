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

package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.set.FirstAllSets;
import com.avispa.parser.precedence.table.set.FirstSets;
import com.avispa.parser.precedence.table.set.LastAllSets;
import com.avispa.parser.precedence.table.set.SimplePrecedenceSets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Getter
public class SimplePrecedenceTable extends PrecedenceTable {
    private final SimplePrecedenceSets firstAll;
    private final SimplePrecedenceSets lastAll;
    private final FirstSets first;

    public SimplePrecedenceTable(Grammar grammar) throws PrecedenceTableException {
        this.firstAll = new FirstAllSets(grammar);
        this.lastAll = new LastAllSets(grammar);
        this.first = new FirstSets(firstAll, grammar.getTerminals());

        try {
            this.table = construct(grammar.getProductions());
        } catch(RelationException e) {
            throw new PrecedenceTableException("Can't create simple precedence table", e);
        }

        if(log.isDebugEnabled()) {
            log.debug("Precedence table:");
            log.debug("{}", this);
        }
    }

    @Override
    protected void addRelations(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        // X ≐ Y
        addEqualsRelation(currentPair, result);

        // X ⋖ FIRST_ALL(Y)
        addLessThanRelation(currentPair, result);

        // LAST_ALL(X) ⋗ FIRST(Y)
        addGreaterThanRelation(currentPair, result);
    }

    @Override
    protected final void addEqualsRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        addRelation(currentPair, Precedence.EQUALS, result);
    }

    @Override
    protected final void addLessThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: {} ⋖ FIRST_ALL({})", currentPair.getLeft(), currentPair.getRight());

        this.firstAll.getFor(currentPair.getRight()).forEach(right -> addRelation(Pair.of(currentPair.getLeft(), right), Precedence.LESS_THAN, result));
    }

    @Override
    protected final void addGreaterThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: LAST_ALL({}) ⋗ FIRST({})", currentPair.getLeft(), currentPair.getRight());

        this.lastAll.getFor(currentPair.getLeft())
                .forEach(left -> this.first.getFor(currentPair.getRight())
                        .forEach(right -> addRelation(Pair.of(left, right), Precedence.GREATER_THAN, result)));
    }
}
