package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
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
    protected final Map<NonTerminal, List<Production>> groupProductionsByLhs(ContextFreeGrammar grammar) {
        return grammar.getProductions()
                .stream()
                .collect(Collectors.groupingBy(Production::getLhs));
    }

    /**
     * Finds first or last token (of any type) from right-hand side tokens
     * @param rhsTokens
     * @return
     */
    protected abstract GenericToken findToken(List<GenericToken> rhsTokens);

    /**
     * Update set by adding token. If this is the first value, create empty set first.
     * @param lhs
     * @param token
     */
    protected final void update(K lhs, V token) {
        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                .add(token);
    }

    /**
     * Update set by adding tokens. If these are the first values, create empty set first.
     * @param lhs
     * @param tokens
     */
    protected final void update(K lhs, Set<V> tokens) {
        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                .addAll(tokens);
    }

    /**
     * Get set for provided token. If set does not exit, empty set is returned.
     * @param token
     * @return
     */
    public final Set<V> getFor(K token) {
        return Collections.unmodifiableSet(this.sets.getOrDefault(token, Set.of()));
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