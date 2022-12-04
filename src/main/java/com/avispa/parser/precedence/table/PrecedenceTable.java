package com.avispa.parser.precedence.table;

import com.avispa.parser.misc.TablePrinter;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class PrecedenceTable<T extends Symbol> {
    protected static final Terminal BOUNDARY_MARKER = Terminal.of("MARKER", "\\$");

    protected Map<Pair<T, T>, Precedence> table;

    public Map<Pair<T, T>, Precedence> get() {
        return table;
    }

    protected abstract Map<Pair<T, T>, Precedence> construct(List<Production> productions, NonTerminal start);

    protected abstract void addEqualsRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<T, T>, Precedence> result);

    protected abstract void addLessThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<T, T>, Precedence> result);

    protected abstract void addGreaterThanRelation(Pair<Symbol, Symbol> currentPair, Map<Pair<T, T>, Precedence> result);

    protected final void addRelation(Pair<T, T> pair, Precedence precedence, Map<Pair<T, T>, Precedence> result) {
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
