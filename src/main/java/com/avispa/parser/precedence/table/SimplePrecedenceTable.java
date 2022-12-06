package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.set.FirstAllSets;
import com.avispa.parser.precedence.table.set.FirstSets;
import com.avispa.parser.precedence.table.set.LastAllSets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static com.avispa.parser.precedence.grammar.Terminal.BOUNDARY_MARKER;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Getter
public class SimplePrecedenceTable extends PrecedenceTable {
    private final FirstAllSets firstAll;
    private final LastAllSets lastAll;
    private final FirstSets first;

    public SimplePrecedenceTable(ContextFreeGrammar cfg) {
        this.firstAll = new FirstAllSets(cfg);
        this.lastAll = new LastAllSets(cfg);
        this.first = new FirstSets(firstAll, cfg.getTerminals());

        this.table = construct(cfg.getProductions(), cfg.getStart());
    }

    @Override
    protected final Map<Pair<Symbol, Symbol>, Precedence> construct(List<Production> productions, NonTerminal start) {
        Map<Pair<Symbol, Symbol>, Precedence> result = construct(productions);

        // $ ⋖ FIRST_ALL(S)
        addLessThanRelationForStartAndMarker(start, result);

        // LAST_ALL(S) ⋗ $
        addGreaterThanRelationForMarkerAndStart(start, result);

        return result;
    }

    @Override
    protected void addRelations(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        // X ≐ Y
        addEqualsRelation(currentPair, result);

        // X ⋖ FIRST_ALL(Y)
        addLessThanRelation(currentPair, result);

        // LAST_ALL(X) ⋗ FIRST(Y)
        addGreaterThanRelation(currentPair, result);
    }

    @Override
    protected final void addEqualsRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        addRelation(currentPair, Precedence.EQUALS, result);
    }

    @Override
    protected final void addLessThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: {} ⋖ FIRST_ALL({})", currentPair.getLeft(), currentPair.getRight());

        this.firstAll.getFor(currentPair.getRight()).forEach(right -> addRelation(Pair.of(currentPair.getLeft(), right), Precedence.LESS_THAN, result));
    }

    @Override
    protected final void addGreaterThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: LAST_ALL({}) ⋗ FIRST({})", currentPair.getLeft(), currentPair.getRight());

        this.lastAll.getFor(currentPair.getLeft())
                .forEach(left -> this.first.getFor(currentPair.getRight())
                        .forEach(right -> addRelation(Pair.of(left, right), Precedence.GREATER_THAN, result)));
    }

    private void addLessThanRelationForStartAndMarker(NonTerminal start, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: $ ⋖ FIRST_ALL({})", start);

        this.firstAll.getFor(start).forEach(right -> {
            log.debug("Adding relation: {} ⋖ {}", BOUNDARY_MARKER, right);
            result.put(Pair.of(BOUNDARY_MARKER, right), Precedence.LESS_THAN);
        });
    }

    private void addGreaterThanRelationForMarkerAndStart(NonTerminal start, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relations: LAST_ALL({}) ⋗ $", start);

        this.lastAll.getFor(start).forEach(left -> {
            log.debug("Adding relation: {} ⋗ {}", left, BOUNDARY_MARKER);

            result.put(Pair.of(left, BOUNDARY_MARKER), Precedence.GREATER_THAN);
        });
    }
}
