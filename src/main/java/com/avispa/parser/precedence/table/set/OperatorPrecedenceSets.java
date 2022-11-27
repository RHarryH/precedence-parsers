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
public abstract class OperatorPrecedenceSets extends PrecedenceSets {
    protected final Map<NonTerminal, Set<Terminal>> sets = new HashMap<>();

    private final String setsName;

    OperatorPrecedenceSets(ContextFreeGrammar grammar, String setsName) {
        this.setsName = setsName;
        log.debug("Constructing {} set for '{}' grammar.", setsName, grammar.getName());
        construct(grammar);
        log.debug("{}", this);
    }

    /**
     * Iterate through all productions to find sets elements
     * @param grammar context free grammar for which sets should be built
     */
    private void construct(ContextFreeGrammar grammar) {
        // group productions by left-hand side non-terminals so alternatives can be processed in single external loop run
        Map<NonTerminal, List<Production>> productionsByLhs =
                groupProductionsByLhs(grammar);

        for(NonTerminal nonTerminal : grammar.getNonTerminals()) {
            constructFor(nonTerminal, productionsByLhs);
        }
    }

    /**
     * Recursively iterate through productions. First and last terminals on the right are added to FIRST_OP and LAST_OP
     * sets. If token is a non-terminal it is recursively checked for it's first and last tokens until end of possible derivation
     * is reached. The algorithm does not do recursive check if non-terminal was already visited.
     *
     * @param lhs current lhs non-terminal
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     */
    private Set<Terminal> constructFor(NonTerminal lhs, Map<NonTerminal, List<Production>> productionsByLhs) {
        log.debug("Started processing of '{}' non-terminal.", lhs);

        Set<Terminal> set = new HashSet<>();
        for (Production production : productionsByLhs.get(lhs)) { // for all alternatives
            set.addAll(downstream(lhs, production.getRhs(), productionsByLhs));
        }
        return set;
    }

    protected final Set<Terminal> downstream(NonTerminal lhs, List<GenericToken> rhsTokens, Map<NonTerminal, List<Production>> productionsByLhs) {
        GenericToken token = rhsTokens.get(rhsTokens.size() - 1);

        Set<Terminal> downstreamTerminals = new HashSet<>();
        if(token instanceof NonTerminal) {
            if(this.sets.containsKey(token)) {
                log.debug("Set for '{}' already exists. It will be reused.", token);
                downstreamTerminals = new HashSet<>(this.sets.get(token)); // make a copy, otherwise set for last token non-terminal will be overwritten
            } else if(lhs != token) {
                log.debug("Set for '{}' does not exists. It will be created by recursive construction.", token);
                downstreamTerminals = constructFor((NonTerminal) token, productionsByLhs);
            }
        }

        Terminal terminal = findTerminal(rhsTokens);
        if(null != terminal) {
            downstreamTerminals.add(terminal); // add found terminal to propagate it upstream
        }

        log.debug("Generated set for '{}' non-terminal: {}", lhs, downstreamTerminals);
        updateLastOp(lhs, downstreamTerminals);

        return downstreamTerminals;
    }

    protected abstract Terminal findTerminal(List<GenericToken> rhsTokens);

    private void updateLastOp(NonTerminal lhs, Set<Terminal> terminals) {
        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                .addAll(terminals);
    }

    public Set<Terminal> getFor(NonTerminal token) {
        return sets.get(token);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String newLine = System.lineSeparator();

        sets.forEach((key, value) -> sb.append(String.format("%s(%s)=%s", setsName, key, value)).append(newLine));

        return sb.toString();
    }
}
