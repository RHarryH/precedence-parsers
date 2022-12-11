package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.set.FirstAllSets;
import com.avispa.parser.precedence.table.set.FirstSets;
import com.avispa.parser.precedence.table.set.LastAllSets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

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

        this.table = construct(cfg.getProductions());

        if(log.isDebugEnabled()) {
            log.debug("Precedence table:");
            log.debug("{}", this);
        }
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
}
