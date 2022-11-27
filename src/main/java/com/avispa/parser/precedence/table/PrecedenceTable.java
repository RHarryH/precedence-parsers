package com.avispa.parser.precedence.table;

import com.avispa.parser.misc.TablePrinter;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
abstract class PrecedenceTable {
    protected static final Terminal BOUNDARY_MARKER = Terminal.of("$", "\\$");

    protected Map<Pair<GenericToken, GenericToken>, Precedence> table;

    public Map<Pair<GenericToken, GenericToken>, Precedence> get() {
        return table;
    }

    protected abstract Map<Pair<GenericToken, GenericToken>, Precedence> build(List<Production> productions, NonTerminal start);

    @Override
    public String toString() {
        Map<Pair<String, String>, String> data = new HashMap<>();
        for(var entry : table.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            data.put(Pair.of(key.getLeft().getValue(), key.getRight().getValue()), value.getSymbol());
        }

        return new TablePrinter(data).print();
    }
}
