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

import com.avispa.parser.misc.ListUtil;
import com.avispa.parser.misc.TablePrinter;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.set.FirstSets;
import com.avispa.parser.precedence.table.set.PrecedenceSets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class PrecedenceTable {
    private final Map<Pair<Symbol, Symbol>, Precedence> table;

    private final PrecedenceSets firstPrecedenceSets; // FIRST_OP/FIRST_ALL
    private final PrecedenceSets lastPrecedenceSets; // LAST_OP/LAST_ALL
    private final PrecedenceSets firstSets; // FIRST

    @Getter
    private final boolean withWeakPrecedenceConflict;
    @Getter
    private final boolean weakPrecedence;

    protected PrecedenceTable(Grammar grammar, PrecedenceSets firstPrecedenceSets, PrecedenceSets lastPrecedenceSets) throws PrecedenceTableException {
        this.firstPrecedenceSets = firstPrecedenceSets;
        this.lastPrecedenceSets = lastPrecedenceSets;
        this.firstSets = new FirstSets(firstPrecedenceSets, grammar.getTerminals());

        try {
            final List<Production> productions = grammar.getProductions();

            this.table = construct(productions);
            this.withWeakPrecedenceConflict = hasLessThanOrEqualsConflict();
            this.weakPrecedence = getWeakPrecedence(productions);
        } catch(RelationException e) {
            throw new PrecedenceTableException("Can't create precedence table", e);
        }

        if(log.isDebugEnabled()) {
            log.debug("Precedence table:");
            log.debug("{}", this);
        }
    }

    protected Map<Pair<Symbol, Symbol>, Precedence> construct(List<Production> productions) {
        Map<Pair<Symbol, Symbol>, Precedence> result = new HashMap<>();

        productions.stream()
                .map(Production::getRhs)
                .flatMap(rhs -> ListUtil.sliding(rhs, 2))
                .map(window -> Pair.of(window.get(0), window.get(1)))
                .forEach(pair -> {
                    log.debug("Sliding pair: {}", pair);
                    addRelations(pair, result);
                });

        return result;
    }

    protected abstract void addRelations(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result);

    protected void addEqualsRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        addRelation(currentPair, Precedence.EQUALS, result);
    }

    protected final void addLessThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: {} ⋖ {}({})", currentPair.getLeft(), currentPair.getRight(), firstPrecedenceSets.getName());

        this.firstPrecedenceSets.getFor(currentPair.getRight())
                .forEach(right -> addRelation(Pair.of(currentPair.getLeft(), right), Precedence.LESS_THAN, result));
    }

    protected final void addGreaterThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: {}({}) ⋗ {}({})", lastPrecedenceSets.getName(), currentPair.getLeft(), firstSets.getName(), currentPair.getRight());

        this.lastPrecedenceSets.getFor(currentPair.getLeft())
                .forEach(left -> this.firstSets.getFor(currentPair.getRight())
                        .forEach(right -> addRelation(Pair.of(left, right), Precedence.GREATER_THAN, result)));
    }

    protected final void addRelation(Pair<Symbol, Symbol> pair, Precedence precedence, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relation: {} {} {}", pair.getLeft(), precedence.getSymbol(), pair.getRight());

        if(result.containsKey(pair)) {
            Precedence currentPrecedence = result.get(pair);
            if(precedence.equals(currentPrecedence)) {
                log.warn("Trying to overwrite existing precedence with the same value. Skipping.");
                return;
            }

            if(currentPrecedence.equals(Precedence.LESS_THAN_OR_EQUALS) &&
                    (precedence.equals(Precedence.LESS_THAN) || precedence.equals(Precedence.EQUALS))) {
                log.warn("There is already {} precedence. Tried to insert {}. Skipping.", currentPrecedence, precedence);
                return;
            }

            if((precedence.equals(Precedence.EQUALS) && currentPrecedence.equals(Precedence.LESS_THAN)) ||
                    (precedence.equals(Precedence.LESS_THAN) && currentPrecedence.equals(Precedence.EQUALS))) {
                log.warn("Weak-precedence grammar detected. There is already {} symbol, while trying to insert {} symbol. Merging precedence symbol to {}", currentPrecedence, precedence, Precedence.LESS_THAN_OR_EQUALS);
                result.put(pair, Precedence.LESS_THAN_OR_EQUALS);
            } else {
                String message = String.format("Conflict detected. Tried to insert %s precedence while there is already %s precedence for %s symbols", precedence, currentPrecedence, pair);
                log.error(message);
                throw new RelationException(message);
            }
        } else {
            result.put(pair, precedence);
        }
    }

    /**
     * @return <code>true</code> when there is a conflict of ⋖ with ≐ in any table cell
     */
    private boolean hasLessThanOrEqualsConflict() {
        return table.values()
                .stream()
                .anyMatch(Precedence.LESS_THAN_OR_EQUALS::equals);
    }

    /**
     * Weak-precedence table is when following condition is met:
     * - A -> aXb and B -> b are productions and there are no relations X ⋖ B and X ≐ B (or simply X ⩿ B). In other words none of the rhs
     * is a tail of another rhs
     *
     * Every simple precedence grammar is a weak-precedence grammar but here true is returned if and only
     * if the table has a conflict of ⋖ with ≐
     * @param productions productions list
     * @return
     */
    private boolean getWeakPrecedence(List<Production> productions) {
        return this.withWeakPrecedenceConflict && matchWeakPrecedenceCondition(productions);
    }

    /**
     * @param productions productions list
     * @return <code>true</code> when any production is a sublist of another and ⩿ relation does not exist for specific
     * symbols
     */
    private boolean matchWeakPrecedenceCondition(List<Production> productions) {
        for(Production sourceProduction : productions) {
            List<Symbol> sourceRhs = sourceProduction.getRhs();
            for(Production comparedProduction : productions) {
                if(sourceProduction == comparedProduction) { // skip same elements
                    continue;
                }

                List<Symbol> comparedRhs = comparedProduction.getRhs();

                if(ListUtil.endsWith(sourceRhs, comparedRhs)) {
                    log.debug("Right-hand side of production '{}' ends with symbols from '{}' production", sourceProduction, comparedProduction);

                    if (hasLessThanOrEqualConflictWithSublist(sourceRhs, comparedProduction)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * @param sourceRhs right-hand side symbols of source production
     * @param comparedProduction compared production
     * @return <code>true</code> when first unique symbol not belonging to sublist does not have ⩿ relation with
     * left-hand side symbol of compared production
     */
    private boolean hasLessThanOrEqualConflictWithSublist(List<Symbol> sourceRhs, Production comparedProduction) {
        List<Symbol> comparedRhs = comparedProduction.getRhs();

        Symbol firstUnique = sourceRhs.get(sourceRhs.size() - comparedRhs.size() - 1);
        log.debug("First symbol not belonging to sublist: {}", firstUnique);
        Symbol comparedLhs = comparedProduction.getLhs();
        log.debug("Left-hand side of compared production: {}", comparedLhs);

        Precedence precedence = table.get(Pair.of(firstUnique, comparedLhs));

        if(null != precedence) {
            log.debug("Precedence for ({}, {}) is {}", firstUnique, comparedLhs, precedence);
            return precedence.equals(Precedence.LESS_THAN_OR_EQUALS);
        }

        log.debug("Precedence for ({}, {}) does not exist", firstUnique, comparedLhs);

        return false;
    }

    public Map<Pair<Symbol, Symbol>, Precedence> get() {
        return table;
    }

    public Precedence get(Symbol a, Symbol b) {
        return table.get(Pair.of(a, b));
    }

    @Override
    public String toString() {
        Map<Pair<String, String>, String> data = new HashMap<>();
        for(var entry : table.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            data.put(Pair.of(key.getLeft().getName(), key.getRight().getName()), value.getSymbol());
        }

        return new TablePrinter(data).print();
    }
}
