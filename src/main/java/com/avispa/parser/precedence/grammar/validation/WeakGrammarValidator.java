package com.avispa.parser.precedence.grammar.validation;

import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;

/**
 * @author Rafał Hiszpański
 */
public interface WeakGrammarValidator extends GrammarValidator {
    default boolean isWeak(PrecedenceTable table) {
        return hasLessThanOrEqualsConflict(table);
    }

    private boolean hasLessThanOrEqualsConflict(PrecedenceTable table) {
        return table.get().values().stream().anyMatch(Precedence.LESS_THAN_OR_EQUALS::equals);
    }
}
