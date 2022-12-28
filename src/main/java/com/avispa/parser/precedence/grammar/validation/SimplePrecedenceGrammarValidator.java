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

package com.avispa.parser.precedence.grammar.validation;

import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Simple precedence grammar is a grammar when precedence table contains all possible symbols and does not have any conflicts.
 * All right-hand sides of productions has to be unique within whole grammar. None of productions right-hand sides
 * can be empty.
 *
 * If there is a conflict of ⋖ with ≐ and none of the rhs is a tail of another rhs then grammar is considered as
 * weak-precedence grammar.
 *
 * Last condition for weak-precedence grammar is not checked. Some sources add additional requirement that if A -> aXb and
 * B -> b are productions then there are no relations X ⋖ B and X ≐ B. Other sources mentions that when there are more
 * than one matching productions, the longest match should be picked by a parser. This approach is used in this application.
 * @author Rafał Hiszpański
 */
@Slf4j
public class SimplePrecedenceGrammarValidator implements GrammarValidator {

    @Override
    public boolean is(Grammar grammar) {
        var productions = grammar.getProductions();

        if(hasEmptyProductions(productions)) {
            log.error("Grammar is not a simple precedence grammar.");
            return false;
        }

        if(!areAllRhsUnique(productions)) {
            log.error("Grammar is not a simple precedence grammar.");
            return false;
        }

        return true;
    }

    private boolean hasEmptyProductions(List<Production> productions) {
        for(Production production : productions) {
            List<Symbol> rhs = production.getRhs();

            if (rhs.isEmpty()) {
                log.error("Empty productions are not all allowed for simple precedence grammars: {}.", production);
                return true;
            }
        }

        return false;
    }

    private boolean areAllRhsUnique(List<Production> productions) {
        long rhsSize = productions.size(); // number of all productions is the same as number of rhs
        long uniqueRhsSize = productions.stream().map(Production::getRhs).distinct().count();
        if(rhsSize != uniqueRhsSize) {
            log.error("Not all right-hand sides of productions are unique.");
            return false;
        }

        return true;
    }
}
