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
