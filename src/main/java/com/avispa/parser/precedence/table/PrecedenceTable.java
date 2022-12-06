package com.avispa.parser.precedence.table;

import com.avispa.parser.misc.ListUtil;
import com.avispa.parser.misc.TablePrinter;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class PrecedenceTable {
    protected Map<Pair<Symbol, Symbol>, Precedence> table;

    protected abstract Map<Pair<Symbol, Symbol>, Precedence> construct(List<Production> productions, NonTerminal start);

    protected Map<Pair<Symbol, Symbol>, Precedence> construct(List<Production> productions) {
        Map<Pair<Symbol, Symbol>, Precedence> result = new HashMap<>();

        productions.stream()
                .map(Production::getRhs)
                .flatMap(rhs -> ListUtil.sliding(rhs, 2))
                .map(window -> Pair.of(window.get(0), window.get(1)))
                .forEach(pair -> {
                    log.debug("Sliding pair: {}", pair);
                    addRelations(pair, result);
                });
        return result;
    }

    protected abstract void addRelations(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result);

    protected abstract void addEqualsRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result);

    protected abstract void addLessThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result);

    protected abstract void addGreaterThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<Symbol, Symbol>, Precedence> result);

    protected final void addRelation(Pair<Symbol, Symbol> pair, Precedence precedence, Map<Pair<Symbol, Symbol>, Precedence> result) {
        log.debug("Adding relation: {} {} {}", pair.getLeft(), precedence.getSymbol(), pair.getRight());

        if(result.containsKey(pair)) {
            Precedence currentPrecedence = result.get(pair);
            if(precedence.equals(currentPrecedence)) {
                log.warn("Trying to overwrite existing precedence with the same value. Skipping.");
                return;
            }

            if(currentPrecedence.equals(Precedence.LESS_THAN_OR_EQUALS) &&
                    (precedence.equals(Precedence.LESS_THAN) || precedence.equals(Precedence.EQUALS))) {
                log.warn("There is already {} precedence. Tried to insert {}. Skipping.", currentPrecedence, precedence);
                return;
            }

            if((precedence.equals(Precedence.EQUALS) && currentPrecedence.equals(Precedence.LESS_THAN)) ||
                    (precedence.equals(Precedence.LESS_THAN) && currentPrecedence.equals(Precedence.EQUALS))) {
                log.warn("Weak-precedence grammar detected. There is already {} symbol, while trying to insert {} symbol. Merging precedence symbol to {}", currentPrecedence, precedence, Precedence.LESS_THAN_OR_EQUALS);
                result.put(pair, Precedence.LESS_THAN_OR_EQUALS);
            } else {
                String message = String.format("Conflict detected. Tried to insert %s precedence while there is already %s precedence for %s symbols", precedence, currentPrecedence, pair);
                log.error(message);
                throw new PrecedenceTableException(message);
            }
        } else {
            result.put(pair, precedence);
        }
    }

    public Map<Pair<Symbol, Symbol>, Precedence> get() {
        return table;
    }

    public Precedence get(Symbol a, Symbol b) {
        return table.get(Pair.of(a, b));
    }

    @Override
    public String toString() {
        Map<Pair<String, String>, String> data = new HashMap<>();
        for(var entry : table.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            data.put(Pair.of(key.getLeft().getName(), key.getRight().getName()), value.getSymbol());
        }

        return new TablePrinter(data).print();
    }
}
