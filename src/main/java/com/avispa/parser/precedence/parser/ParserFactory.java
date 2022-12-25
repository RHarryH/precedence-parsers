package com.avispa.parser.precedence.parser;

import com.avispa.parser.precedence.function.GraphPrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctionsException;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.validation.OperatorGrammarValidator;
import com.avispa.parser.precedence.grammar.validation.SimplePrecedenceGrammarValidator;
import com.avispa.parser.precedence.grammar.validation.WeakGrammarValidator;
import com.avispa.parser.precedence.table.OperatorPrecedenceTable;
import com.avispa.parser.precedence.table.PrecedenceTable;
import com.avispa.parser.precedence.table.PrecedenceTableException;
import com.avispa.parser.precedence.table.SimplePrecedenceTable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParserFactory {

    /**
     * Creates new operator-precedence parser for grammar if it fulfills requirements of operator-precedence grammar.
     * If created precedence table will have a conflict, operator-precedence table can't be created. Precedence
     * functions will always be created and used.
     *
     * Grammar is an operator-precedence grammar when it is an operator grammar, and it is possible to construct and
     * operator-precedence table without conflicts (when there are existing more than one precedence for any pair of symbols).
     *
     * @param grammar formal grammar matching requirements for operator-precedence grammar
     * @return new operator-precedence parser instance
     */
    public static OperatorPrecedenceParser newOperatorPrecedenceParser(Grammar grammar) {
        return newOperatorPrecedenceParser(grammar, true);
    }

    /**
     * Creates new operator-precedence parser for grammar if it fulfills requirements of operator-precedence grammar.
     * If created precedence table will have a conflict, operator-precedence table can't be created.
     *
     * Grammar is an operator-precedence grammar when it is an operator grammar, and it is possible to construct and
     * operator-precedence table without conflicts (when there are existing more than one precedence for any pair of symbols).
     *
     * @param grammar formal grammar matching requirements for operator-precedence grammar
     * @param usePrecedenceFunctions true to use precedence functions
     * @return new operator-precedence parser instance
     */
    public static OperatorPrecedenceParser newOperatorPrecedenceParser(Grammar grammar, boolean usePrecedenceFunctions) {
        WeakGrammarValidator validator = new OperatorGrammarValidator();

        try {
            PrecedenceTable table = new OperatorPrecedenceTable(grammar);

            if (!validator.isWeak(table)) {
                if (validator.is(grammar)) {
                    if(usePrecedenceFunctions) {
                        PrecedenceFunctions functions = getPrecedenceFunctions(table);
                        return new OperatorPrecedenceParser(grammar, table, functions);
                    } else {
                        return new OperatorPrecedenceParser(grammar, table);
                    }
                }
            } else {
                log.error("Weak-precedence detected. It is not supported for operator-precedence grammars.");
            }
        } catch (PrecedenceTableException e) {
            log.error(e.getMessage());
            log.error("Grammar is not an operator-precedence grammar as operator-precedence table cannot be constructed.");
        }

        return null;
    }

    /**
     * Creates new simple precedence parser for grammar if it fulfills requirements of simple precedence grammar.
     * Precedence functions will be used if and only if the grammar is not a weak precedence grammar.
     *
     * @param grammar formal grammar matching requirements for simple precedence grammar
     * @return new simple precedence parser instance
     */
    public static SimplePrecedenceParser newSimplePrecedenceParser(Grammar grammar) {
        return newSimplePrecedenceParser(grammar, true);
    }

    /**
     * Creates new simple precedence parser for grammar if it fulfills requirements of simple precedence grammar.
     * Precedence functions can be disabled, however when used they will be used if the grammar is not
     * a weak precedence grammar.
     *
     * @param grammar formal grammar matching requirements for simple precedence grammar
     * @param usePrecedenceFunctions true to use precedence functions
     * @return new simple precedence parser instance
     */
    public static SimplePrecedenceParser newSimplePrecedenceParser(Grammar grammar, boolean usePrecedenceFunctions) {
        WeakGrammarValidator validator = new SimplePrecedenceGrammarValidator();

        try {
            PrecedenceTable table = new SimplePrecedenceTable(grammar);

            if (validator.is(grammar)) {
                if(usePrecedenceFunctions) {
                    PrecedenceFunctions functions = null;

                    if (validator.isWeak(table)) {
                        log.warn("Precedence functions won't be calculated because weak-precedence grammar was detected.");
                    } else {
                        functions = getPrecedenceFunctions(table);
                    }

                    return new SimplePrecedenceParser(grammar, table, functions);
                } else {
                    return new SimplePrecedenceParser(grammar, table);
                }
            }
        } catch (PrecedenceTableException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private static PrecedenceFunctions getPrecedenceFunctions(PrecedenceTable table) {
        PrecedenceFunctions functions = null;
        try {
            functions = new GraphPrecedenceFunctions(table);
        } catch (PrecedenceFunctionsException e) {
            log.warn("Precedence functions can't be calculated. Precedence table will be used instead.", e);
        }
        return functions;
    }
}
