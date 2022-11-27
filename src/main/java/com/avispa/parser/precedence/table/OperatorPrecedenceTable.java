package com.avispa.parser.precedence.table;

import com.avispa.parser.misc.ListUtil;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.precedence.table.set.FirstOpSets;
import com.avispa.parser.precedence.table.set.LastOpSets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Slf4j
public class OperatorPrecedenceTable extends PrecedenceTable<Terminal> {
    private final FirstOpSets firstOp;
    private final LastOpSets lastOp;

    public OperatorPrecedenceTable(ContextFreeGrammar cfg) {
        this.firstOp = new FirstOpSets(cfg);
        this.lastOp = new LastOpSets(cfg);

        this.table = build(cfg.getProductions(), cfg.getStart());
    }

    @Override
    protected final Map<Pair<Terminal, Terminal>, Precedence> build(List<Production> productions, NonTerminal start) {
        Map<Pair<Terminal, Terminal>, Precedence> result = new HashMap<>();

        productions.stream()
                .map(Production::getRhs)
                .flatMap(rhs -> ListUtil.sliding(rhs, 2))
                .map(window -> Pair.of(window.get(0), window.get(1)))
                .forEach(pair -> {
                    log.debug("Sliding pair: {}", pair);
                    addRelations(pair, result);
                });

        // X ≐ Y when XZY (terminal, non-terminal, terminal)
        addEqualsForTriple(productions, result);

        // $ ⋖ FIRST_OP(S)
        addLessThanRelationForStartAndMarker(start, result);

        // LAST_OP(S) ⋗ $
        addGreaterThanRelationForMarkerAndStart(start, result);

        return result;
    }

    private void addRelations(Pair<GenericToken, GenericToken> currentPair, Map<Pair<Terminal, Terminal>, Precedence> result) {
        var left = currentPair.getLeft();
        var right = currentPair.getRight();

        if(Terminal.isOf(left) && Terminal.isOf(right)) {
            // X ≐ Y when XY (terminal, terminal)
            addEqualsRelation(currentPair, result);
        } else if(Terminal.isOf(left) && NonTerminal.isOf(right)) {
            // X ⋖ FIRST_OP(Y) when XY (terminal, non-terminal)
            addLessThanRelation(currentPair, result);
        } else if(NonTerminal.isOf(left) && Terminal.isOf(right)) {
            // LAST_OP(Y) ⋗ X when YX (non-terminal, terminal)
            addGreaterThanRelation(currentPair, result);
        } else {
            throw new PrecedenceTableException("Grammar is not an operator grammar as adjacent non-terminals were found");
        }
    }

    private void addEqualsForTriple(List<Production> productions, Map<Pair<Terminal, Terminal>, Precedence> result) {
        productions.stream()
                .map(Production::getRhs)
                .flatMap(rhs -> ListUtil.sliding(rhs, 3))
                .map(window -> Triple.of(window.get(0), window.get(1), window.get(2)))
                .forEach(triple -> {
                    log.debug("Sliding triple: {}", triple);
                    if(Terminal.isOf(triple.getLeft()) && NonTerminal.isOf(triple.getMiddle()) && Terminal.isOf(triple.getRight())) {
                        addEqualsRelation(Pair.of(triple.getLeft(), triple.getRight()), result);
                    }
                });
    }

    @Override
    protected final void addEqualsRelation(Pair<GenericToken, GenericToken> currentPair, Map<Pair<Terminal, Terminal>, Precedence> result) {
        var castedPair = Pair.of((Terminal)currentPair.getLeft(), (Terminal)currentPair.getRight());
        addRelation(castedPair, Precedence.EQUALS, result);
    }

    @Override
    protected final void addLessThanRelation(Pair<GenericToken, GenericToken> currentPair, Map<Pair<Terminal, Terminal>, Precedence> result) {
        log.debug("Adding relations: {} ⋖ FIRST_OP({})", currentPair.getLeft(), currentPair.getRight());

        this.firstOp.getFor((NonTerminal) currentPair.getRight())
                .forEach(right -> addRelation(Pair.of((Terminal) currentPair.getLeft(), right), Precedence.LESS_THAN, result));
    }

    @Override
    protected final void addGreaterThanRelation(Pair<GenericToken, GenericToken> currentPair, Map<Pair<Terminal, Terminal>, Precedence> result) {
        log.debug("Adding relations: LAST_OP({}) ⋗ {}", currentPair.getLeft(), currentPair.getRight());

        this.lastOp.getFor((NonTerminal) currentPair.getLeft())
                .forEach(left -> addRelation(Pair.of(left, (Terminal) currentPair.getRight()), Precedence.GREATER_THAN, result));
    }

    private void addLessThanRelationForStartAndMarker(NonTerminal start, Map<Pair<Terminal, Terminal>, Precedence> result) {
        log.debug("Adding relations: $ ⋖ FIRST_OP({})", start);

        this.firstOp.getFor(start).forEach(right -> {
            log.debug("Adding relation: {} ⋖ {}", BOUNDARY_MARKER, right);
            result.put(Pair.of(BOUNDARY_MARKER, right), Precedence.LESS_THAN);
        });
    }

    private void addGreaterThanRelationForMarkerAndStart(NonTerminal start, Map<Pair<Terminal, Terminal>, Precedence> result) {
        log.debug("Adding relations: LAST_OP({}) ⋗ $", start);

        this.lastOp.getFor(start).forEach(left -> {
            log.debug("Adding relation: {} ⋗ {}", left, BOUNDARY_MARKER);

            result.put(Pair.of(left, BOUNDARY_MARKER), Precedence.GREATER_THAN);
        });
    }
}
