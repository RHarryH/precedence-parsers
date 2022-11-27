package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
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
abstract class PrecedenceSets<K, V> {
    protected final Map<K, Set<V>> sets = new HashMap<>();
    protected final String setsName;

    PrecedenceSets(String setsName) {
        this.setsName = setsName;
    }

    protected final Map<NonTerminal, List<Production>> groupProductionsByLhs(ContextFreeGrammar grammar) {
        return grammar.getProductions()
                .stream()
                .collect(Collectors.groupingBy(Production::getLhs));
    }

    protected final void update(K lhs, V token) {
        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                .add(token);
    }

    protected final void update(K lhs, Set<V> tokens) {
        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                .addAll(tokens);
    }

    public final Set<V> getFor(K token) {
        return this.sets.get(token);
    }

    public final Map<K, Set<V>> get() {
        return Collections.unmodifiableMap(this.sets);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String newLine = System.lineSeparator();

        this.sets.forEach((key, value) -> sb.append(String.format("%s(%s)=%s", setsName, key, value)).append(newLine));

        return sb.toString();
    }
}