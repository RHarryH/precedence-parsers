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

package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.Symbol;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class GraphNode {
    private final Set<Symbol> fSymbols = new HashSet<>();
    private final Set<Symbol> gSymbols = new HashSet<>();

    public static GraphNode ofF(Symbol symbol) {
        return new GraphNode().addF(symbol);
    }

    public static GraphNode ofG(Symbol symbol) {
        return new GraphNode().addG(symbol);
    }

    public GraphNode addF(Symbol symbol) {
        this.fSymbols.add(symbol);
        return this;
    }

    public GraphNode addG(Symbol symbol) {
        this.gSymbols.add(symbol);
        return this;
    }

    public boolean containsF(Symbol symbol) {
        return this.fSymbols.contains(symbol);
    }

    public boolean containsG(Symbol symbol) {
        return this.gSymbols.contains(symbol);
    }

    public Set<Symbol> getFSet() {
        return Collections.unmodifiableSet(fSymbols);
    }

    public Set<Symbol> getGSet() {
        return Collections.unmodifiableSet(gSymbols);
    }

    @Override
    public String toString() {
        return Stream.of(fSymbols, gSymbols).collect(Collectors.toSet()).toString();
    }
}
