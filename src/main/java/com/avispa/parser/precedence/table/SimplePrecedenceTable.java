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

package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.set.FirstAllSets;
import com.avispa.parser.precedence.table.set.LastAllSets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class SimplePrecedenceTable extends PrecedenceTable {
    public SimplePrecedenceTable(Grammar grammar) throws PrecedenceTableException {
        super(grammar, new FirstAllSets(grammar), new LastAllSets(grammar));
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
}
