/*
 * MIT License
 *
 * Copyright (c) 2022 Rafał Hiszpański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.avispa.parser.precedence.parser;

import com.avispa.parser.precedence.function.GraphPrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctionsException;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.validation.GrammarValidator;
import com.avispa.parser.precedence.grammar.validation.OperatorGrammarValidator;
import com.avispa.parser.precedence.grammar.validation.SimplePrecedenceGrammarValidator;
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
    public static OperatorPrecedenceParser newOperatorPrecedenceParser(Grammar grammar) throws ParserCreationException {
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
    public static OperatorPrecedenceParser newOperatorPrecedenceParser(Grammar grammar, boolean usePrecedenceFunctions) throws ParserCreationException {
        GrammarValidator validator = new OperatorGrammarValidator();

        try {
            PrecedenceTable table = new OperatorPrecedenceTable(grammar);

            if (!table.isWeakPrecedence()) {
                if (validator.is(grammar)) {
                    if(usePrecedenceFunctions) {
                        PrecedenceFunctions functions = getPrecedenceFunctions(table);
                        return new OperatorPrecedenceParser(grammar, table, functions);
                    } else {
                        return new OperatorPrecedenceParser(grammar, table);
                    }
                } else {
                    throw new ParserCreationException("Grammar is not a valid operator-precedence grammar");
                }
            } else {
                throw new ParserCreationException("Weak-precedence detected in precedence table. It is not supported for operator-precedence grammars.");
            }
        } catch (PrecedenceTableException e) {
            throw new ParserCreationException("Parser can't be created", e);
        }
    }

    /**
     * Creates new simple precedence parser for grammar if it fulfills requirements of simple precedence grammar.
     * Precedence functions will be used if and only if the grammar is not a weak precedence grammar.
     *
     * @param grammar formal grammar matching requirements for simple precedence grammar
     * @return new simple precedence parser instance
     */
    public static SimplePrecedenceParser newSimplePrecedenceParser(Grammar grammar) throws ParserCreationException {
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
    public static SimplePrecedenceParser newSimplePrecedenceParser(Grammar grammar, boolean usePrecedenceFunctions) throws ParserCreationException {
        GrammarValidator validator = new SimplePrecedenceGrammarValidator();

        try {
            PrecedenceTable table = new SimplePrecedenceTable(grammar);

            if (validator.is(grammar)) {
                if(table.isWithWeakPrecedenceConflict()) {
                    if(table.isWeakPrecedence()) {
                        log.warn("Precedence functions won't be calculated because precedence table is of a weak-precedence.");
                        return new SimplePrecedenceParser(grammar, table);
                    } else {
                        throw new ParserCreationException("Weak-precedence conflict detected but grammar is not weak-precedence");
                    }
                } else if(usePrecedenceFunctions) {
                    PrecedenceFunctions functions = getPrecedenceFunctions(table);
                    return new SimplePrecedenceParser(grammar, table, functions);
                } else {
                    return new SimplePrecedenceParser(grammar, table);
                }
            } else {
                throw new ParserCreationException("Grammar is not a valid simple precedence grammar");
            }
        } catch (PrecedenceTableException e) {
            throw new ParserCreationException("Parser can't be created", e);
        }
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
