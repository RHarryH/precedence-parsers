package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.GenericToken;
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
    private final Set<GenericToken> fSymbols = new HashSet<>();
    private final Set<GenericToken> gSymbols = new HashSet<>();

    public static GraphNode ofF(GenericToken token) {
        return new GraphNode().addF(token);
    }

    public static GraphNode ofG(GenericToken token) {
        return new GraphNode().addG(token);
    }

    public GraphNode addF(GenericToken token) {
        this.fSymbols.add(token);
        return this;
    }

    public GraphNode addG(GenericToken token) {
        this.gSymbols.add(token);
        return this;
    }

    public boolean containsF(GenericToken token) {
        return this.fSymbols.contains(token);
    }

    public boolean containsG(GenericToken token) {
        return this.gSymbols.contains(token);
    }

    public Set<GenericToken> getFSet() {
        return Collections.unmodifiableSet(fSymbols);
    }

    public Set<GenericToken> getGSet() {
        return Collections.unmodifiableSet(gSymbols);
    }

    @Override
    public String toString() {
        return Stream.of(fSymbols, gSymbols).collect(Collectors.toSet()).toString();
    }
}
