package com.avispa.parser.precedence.table;

import com.avispa.parser.misc.ListUtil;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.table.set.SimplePrecedenceSets;
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
public final class SimplePrecedenceTable extends PrecedenceTable{
    @Getter
    private final SimplePrecedenceSets sets;

    public SimplePrecedenceTable(ContextFreeGrammar cfg) {
        this.sets = new SimplePrecedenceSets(cfg);
        this.table = build(cfg.getProductions(), cfg.getStart());
    }

    @Override
    protected Map<Pair<GenericToken, GenericToken>, Precedence> build(List<Production> productions, NonTerminal start) {
        Map<Pair<GenericToken, GenericToken>, Precedence> result = new HashMap<>();

        productions.stream()
                .map(Production::getRhs)
                .flatMap(rhs -> ListUtil.sliding(rhs, 2))
                .map(window -> Pair.of(window.get(0), window.get(1)))
                .forEach(pair -> {
                    log.debug("Sliding pair: {}", pair);
                    addRelations(pair, result);
                });

        // $ ⋖ FIRST_ALL(S)
        addLessThanRelationForStartAndMarker(start, result);

        // LAST_ALL(S) ⋗ $
        addGreaterThanRelationForMarkerAndStart(start, result);

        return result;
    }

    private void addRelations(Pair<GenericToken, GenericToken> currentPair, Map<Pair<GenericToken, GenericToken>, Precedence> result) {
        // X ≐ Y
        addEqualsRelation(currentPair, result);

        // X ⋖ FIRST_ALL(Y)
        addLessThanRelation(currentPair, result);

        // LAST_ALL(X) ⋗ FIRST(Y)
        addGreaterThanRelation(currentPair, result);
    }

    private void addEqualsRelation(Pair<GenericToken, GenericToken> currentPair, Map<Pair<GenericToken, GenericToken>, Precedence> result) {
        addRelation(currentPair, Precedence.EQUALS, result);
    }

    private void addLessThanRelation(Pair<GenericToken, GenericToken> currentPair, Map<Pair<GenericToken, GenericToken>, Precedence> result) {
        log.debug("Adding relations: {} ⋖ FIRST_ALL({})", currentPair.getLeft(), currentPair.getRight());

        this.sets.getFirstAllFor(currentPair.getRight()).forEach(right -> addRelation(Pair.of(currentPair.getLeft(), right), Precedence.LESS_THAN, result));
    }

    private void addGreaterThanRelation(Pair<GenericToken, GenericToken> currentPair, Map<Pair<GenericToken, GenericToken>, Precedence> result) {
        log.debug("Adding relations: LAST_ALL({}) ⋗ FIRST({})", currentPair.getLeft(), currentPair.getRight());

        this.sets.getLastAllFor(currentPair.getLeft())
                .forEach(left -> this.sets.getFirstFor(currentPair.getRight())
                        .forEach(right -> addRelation(Pair.of(left, right), Precedence.GREATER_THAN, result)));
    }

    private void addRelation(Pair<GenericToken, GenericToken> pair, Precedence precedence, Map<Pair<GenericToken, GenericToken>, Precedence> result) {
        log.debug("Adding relation: {} {} {}", pair.getLeft(), precedence.getSymbol(), pair.getRight());

        if(result.containsKey(pair)) {
            Precedence currentPrecedence = result.get(pair);
            if(precedence.equals(currentPrecedence)) {
                log.warn("Trying to overwrite existing precedence with the same value. Skipping");
                return;
            }

            if((precedence.equals(Precedence.EQUALS) && currentPrecedence.equals(Precedence.LESS_THAN)) ||
                    (precedence.equals(Precedence.LESS_THAN) && currentPrecedence.equals(Precedence.EQUALS))) {
                log.warn("Weak-precedence grammar detected. There is already {} symbol, while trying to insert {} symbol. Merging precedence symbol to {}", currentPrecedence, precedence, Precedence.LESS_THAN_OR_EQUALS);
                result.put(pair, Precedence.LESS_THAN_OR_EQUALS);
            } else {
                String message = String.format("Algorithm tried to insert %s precedence while there is already %s precedence for %s tokens", precedence, currentPrecedence, pair);
                log.error(message);
                throw new UnresolvablePrecedenceConflictException(message);
            }
        } else {
            result.put(pair, precedence);
        }
    }

    private void addLessThanRelationForStartAndMarker(NonTerminal start, Map<Pair<GenericToken, GenericToken>, Precedence> result) {
        log.debug("Adding relations: $ ⋖ FIRST_ALL({})", start);

        this.sets.getFirstAllFor(start).forEach(right -> {
            log.debug("Adding relation: {} ⋖ {}", BOUNDARY_MARKER, right);
            result.put(Pair.of(BOUNDARY_MARKER, right), Precedence.LESS_THAN);
        });
    }

    private void addGreaterThanRelationForMarkerAndStart(NonTerminal start, Map<Pair<GenericToken, GenericToken>, Precedence> result) {
        log.debug("Adding relations: LAST_ALL({}) ⋗ $", start);

        this.sets.getLastAllFor(start).forEach(left -> {
            log.debug("Adding relation: {} ⋗ {}", left, BOUNDARY_MARKER);

            result.put(Pair.of(left, BOUNDARY_MARKER), Precedence.GREATER_THAN);
        });
    }
}
