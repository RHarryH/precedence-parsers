package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

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
public class FirstLastSets {
    private final Map<GenericToken, Set<GenericToken>> firstAll = new HashMap<>();
    private final Map<GenericToken, Set<GenericToken>> lastAll = new HashMap<>();
    private final Map<GenericToken, Set<Terminal>> first = new HashMap<>();

    FirstLastSets(ContextFreeGrammar contextFreeGrammar) {
        initializeSets(contextFreeGrammar.getTerminals());

        // group productions by left-hand side non-terminals so alternatives can be processed in single external loop run
        Map<NonTerminal, List<Production>> productionsByLhs =
                contextFreeGrammar.getProductions()
                        .stream()
                        .collect(Collectors.groupingBy(Production::getLhs));

        findFirstAllAndLastAll(productionsByLhs);
        findFirst();
    }

    /**
     * Add empty list for terminals for FIRST_ALL and LAST_ALL and add self to the FIRST set
     * @param terminals
     */
    private void initializeSets(Set<Terminal> terminals) {
        for(Terminal terminal : terminals) {
            this.firstAll.put(terminal, Set.of());
            this.lastAll.put(terminal, Set.of());
            this.first.put(terminal, Set.of(terminal));
        }
    }

    /**
     * Iterate through all productions to find FIRST_ALL and LAST_ALL sets elements
     * @param productionsByLhs
     */
    private void findFirstAllAndLastAll(Map<NonTerminal, List<Production>> productionsByLhs) {
        for(var productionsEntry : productionsByLhs.entrySet()) { // for all lhs
            Set<NonTerminal> visited = new HashSet<>();
            processProductions(productionsByLhs, productionsEntry.getKey(), productionsEntry.getKey(), productionsEntry.getValue(), visited);
        }
    }

    /**
     * Recursively iterate through productions. First and last tokens  on the right are added to FIRST_ALL and LAST_ALL
     * sets. If token is a non-terminal it is recursively checked for it's first and last tokens until end of possible derivation
     * is reached. The algorithm does not do recursive check if non-terminal was already visited
     *
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     * @param topLhs non-terminal for which FIRST_ALL and LAST_ALL are built
     * @param currentLhs currently visited production's lhs
     * @param currentRhsProductions currently visited production's rhs
     * @param visited set of already visited nodes for top lhs non-terminal
     */
    private void processProductions(Map<NonTerminal, List<Production>> productionsByLhs, NonTerminal topLhs, NonTerminal currentLhs, List<Production> currentRhsProductions, Set<NonTerminal> visited) {
        visited.add(currentLhs); // do not visit already visiting token to avoid endless loop

        for (Production production : currentRhsProductions) { // for all alternatives
            List<GenericToken> rhsTokens = production.getRhs();

            GenericToken firstToken = rhsTokens.get(0);
            GenericToken lastToken = rhsTokens.get(rhsTokens.size() - 1);

            updateFirstAll(topLhs, firstToken);
            if(firstToken instanceof NonTerminal && !visited.contains(firstToken)) {
                processProductions(productionsByLhs, topLhs, (NonTerminal) firstToken, productionsByLhs.get(firstToken), visited);
            }

            updateLastAll(topLhs, lastToken);
            if(lastToken instanceof NonTerminal && !visited.contains(lastToken)) {
                processProductions(productionsByLhs, topLhs, (NonTerminal) lastToken, productionsByLhs.get(lastToken), visited);
            }
        }
    }

    private void updateFirstAll(NonTerminal lhs, GenericToken firstToken) {
        this.firstAll.computeIfAbsent(lhs, key -> new HashSet<>())
                .add(firstToken);
    }

    private void updateLastAll(NonTerminal lhs, GenericToken lastToken) {
        this.lastAll.computeIfAbsent(lhs, key -> new HashSet<>())
                .add(lastToken);
    }

    /**
     * Updates FIRST set by adding  entries for non-terminals. FIRST for non-terminal is a FIRST_ALL with terminals only.
     */
    private void findFirst() {
        for(var firstAllForToken : firstAll.entrySet()) {
            GenericToken lhs = firstAllForToken.getKey();
            if(lhs instanceof NonTerminal) {
                for(GenericToken token : firstAllForToken.getValue()) {
                    if(token instanceof Terminal) {
                        this.first.computeIfAbsent(lhs, key -> new HashSet<>())
                                .add((Terminal) token);
                    }
                }
            }
        }
    }

    public Set<GenericToken> getFirstAllFor(GenericToken token) {
        return firstAll.get(token);
    }

    public Set<GenericToken> getLastAllFor(GenericToken token) {
        return lastAll.get(token);
    }

    public Set<Terminal> getFirstFor(GenericToken token) {
        return first.get(token);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String newLine = System.lineSeparator();

        firstAll.forEach((key, value) -> sb.append(String.format("FIRST_ALL(%s)=%s", key, value)).append(newLine));
        lastAll.forEach((key, value) -> sb.append(String.format("LAST_ALL(%s)=%s", key, value)).append(newLine));
        first.forEach((key, value) -> sb.append(String.format("FIRST(%s)=%s", key, value)).append(newLine));

        return sb.toString();
    }
}
