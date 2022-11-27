package com.avispa.parser.precedence.table.set;

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

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public final class SimplePrecedenceSets extends PrecedenceSets {
    private final Map<GenericToken, Set<GenericToken>> firstAll = new HashMap<>();
    private final Map<GenericToken, Set<GenericToken>> lastAll = new HashMap<>();
    private final Map<GenericToken, Set<Terminal>> first = new HashMap<>();

    public SimplePrecedenceSets(ContextFreeGrammar grammar) {
        initializeSets(grammar.getTerminals());

        processProductions(grammar);

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
     * Iterate through all productions to find sets elements
     * @param grammar context free grammar for which sets should be built
     */
    private void processProductions(ContextFreeGrammar grammar) {
        // group productions by left-hand side non-terminals so alternatives can be processed in single external loop run
        Map<NonTerminal, List<Production>> productionsByLhs =
                groupProductionsByLhs(grammar);

        for(var productionsEntry : productionsByLhs.entrySet()) { // for all lhs
            NonTerminal lhs = productionsEntry.getKey();
            List<Production> productions = productionsEntry.getValue();

            log.debug("Started processing of {} non-terminal.", lhs);
            processProductionsFirst(productionsByLhs, lhs, lhs, productions, new HashSet<>());
            processProductionsLast(productionsByLhs, lhs, lhs, productions, new HashSet<>());
        }
    }

    /**
     * Recursively iterate through productions. First token on the right is added to FIRST_ALL set. If token is a non-terminal
     * it is recursively checked for it's first token until end of possible derivation is reached. The algorithm does not do
     * recursive check if non-terminal was already visited.
     *
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     * @param topLhs non-terminal for which FIRST_ALL is built
     * @param currentLhs currently visited production's lhs
     * @param currentRhsProductions currently visited production's rhs
     * @param visited set of already visited nodes for top lhs non-terminal
     */
    private void processProductionsFirst(Map<NonTerminal, List<Production>> productionsByLhs, NonTerminal topLhs, NonTerminal currentLhs, List<Production> currentRhsProductions, Set<NonTerminal> visited) {
        visited.add(currentLhs); // do not visit already visiting token to avoid endless loop

        for (Production production : currentRhsProductions) { // for all alternatives
            log.debug("Checking {} production for FIRST_ALL set.", production);
            List<GenericToken> rhsTokens = production.getRhs();

            GenericToken firstToken = rhsTokens.get(0);
            log.debug("First token for {} production is: {}. Adding to FIRST_ALL set.", production, firstToken);

            updateFirstAll(topLhs, firstToken);
            if(firstToken instanceof NonTerminal && !visited.contains(firstToken)) {
                log.debug("Token {} is a non-terminal and wasn't visited before. Check it recursively for it's first token.", firstToken);
                processProductionsFirst(productionsByLhs, topLhs, (NonTerminal) firstToken, productionsByLhs.get(firstToken), visited);
            } else {
                log.debug("Token {} is a terminal or was visited before. Skipping.", firstToken);
            }
        }
    }

    private void updateFirstAll(NonTerminal lhs, GenericToken firstToken) {
        this.firstAll.computeIfAbsent(lhs, key -> new HashSet<>())
                .add(firstToken);
    }

    /**
     * Recursively iterate through productions. Last token on the right is added to LAST_ALL set. If token is a non-terminal
     * it is recursively checked for it's last token until end of possible derivation is reached. The algorithm does not do
     * recursive check if non-terminal was already visited.
     *
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     * @param topLhs non-terminal for which LAST_ALL is built
     * @param currentLhs currently visited production's lhs
     * @param currentRhsProductions currently visited production's rhs
     * @param visited set of already visited nodes for top lhs non-terminal
     */
    private void processProductionsLast(Map<NonTerminal, List<Production>> productionsByLhs, NonTerminal topLhs, NonTerminal currentLhs, List<Production> currentRhsProductions, Set<NonTerminal> visited) {
        visited.add(currentLhs); // do not visit already visiting token to avoid endless loop

        for (Production production : currentRhsProductions) { // for all alternatives
            log.debug("Checking {} production for LAST_ALL set.", production);
            List<GenericToken> rhsTokens = production.getRhs();

            GenericToken lastToken = rhsTokens.get(rhsTokens.size() - 1);
            log.debug("Last token for {} production is {}. Adding to LAST_ALL set.", production, lastToken);

            updateLastAll(topLhs, lastToken);
            if(lastToken instanceof NonTerminal && !visited.contains(lastToken)) {
                log.debug("Token {} is a non-terminal and wasn't visited before. Check it recursively for it's first token.", lastToken);
                processProductionsLast(productionsByLhs, topLhs, (NonTerminal) lastToken, productionsByLhs.get(lastToken), visited);
            } else {
                log.debug("Token {} is a terminal or was visited before. Skipping.", lastToken);
            }
        }
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
