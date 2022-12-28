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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class PrecedenceSets<K, V> {
    protected final Map<K, Set<V>> sets = new HashMap<>();
    @Getter
    protected final String name;

    PrecedenceSets(String name) {
        this.name = name;
    }

    /**
     * Groups productions by left-hand side. This groups all alternatives into single entry.
     *
     * Input:
     * E -> A | a;
     * E -> Ac;
     * A -> bc;
     * Output:
     * key=E, value=[A, a, Ac]
     * key=A, value=[bc]
     * @param grammar
     * @return
     */
    protected final Map<NonTerminal, List<Production>> groupProductionsByLhs(Grammar grammar) {
        return grammar.getProductions()
                .stream()
                .collect(Collectors.groupingBy(Production::getLhs));
    }

    /**
     * Finds first or last symbol (of any type) from right-hand side symbols
     * @param rhsSymbols
     * @return
     */
    protected abstract Symbol findSymbol(List<Symbol> rhsSymbols);

    /**
     * Update set by adding symbol. If this is the first value, create empty set first.
     * @param lhs
     * @param symbol
     */
    protected final void update(K lhs, V symbol) {
        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                .add(symbol);
    }

    /**
     * Update set by adding symbols. If these are the first values, create empty set first.
     * @param lhs
     * @param symbols
     */
    protected final void update(K lhs, Set<V> symbols) {
        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                .addAll(symbols);
    }

    /**
     * Get set for provided symbol. If set does not exit, empty set is returned.
     * @param symbol
     * @return
     */
    public final Set<V> getFor(K symbol) {
        return Collections.unmodifiableSet(this.sets.getOrDefault(symbol, Set.of()));
    }

    /**
     * Get all generated sets
     * @return
     */
    public final Map<K, Set<V>> get() {
        return Collections.unmodifiableMap(this.sets);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String newLine = System.lineSeparator();

        this.sets.forEach((key, value) -> sb.append(String.format("%s(%s)=%s", name, key, value)).append(newLine));

        return sb.toString();
    }
}