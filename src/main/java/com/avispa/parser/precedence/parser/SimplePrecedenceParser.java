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


import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
public class SimplePrecedenceParser extends PrecedenceParser<Production> {
    private final TreeNode<Symbol> productionsTree;

    SimplePrecedenceParser(Grammar grammar, PrecedenceTable table) {
        this(grammar, table, null);
    }

    SimplePrecedenceParser(Grammar grammar, PrecedenceTable table, PrecedenceFunctions functions) {
        super(grammar, table, functions);

        this.productionsTree =  ProductionsTreeBuilder.build(grammar.getProductions());
    }

    @Override
    protected void reduce(List<Production> output, Deque<Symbol> symbolStack) throws SyntaxException {
        log.debug("REDUCE (> relation matched).");
        try {
            doReduce(output, symbolStack);
        } catch (ReductionException e){
            throw new SyntaxException("No matching production: " + e.getMessage());
        }
    }

    private void doReduce(List<Production> output, Deque<Symbol> symbolStack) {
        Symbol fromStack;
        Symbol stackTop;
        TreeNode<Symbol> currentNode = productionsTree;
        List<Symbol> rhs = new ArrayList<>();

        do {
            fromStack = symbolStack.pop();
            stackTop = symbolStack.peek();
            if(null == stackTop) {
                throw new ReductionException("Stack is empty. At least boundary marker is expected to be left");
            }

            rhs.add(0, fromStack); // add always to the beginning so there is no need to reverse list later

            Symbol terminal = fromStack.unwrap();
            Symbol currentSymbol = currentNode.getValue();

            currentNode = currentNode.getChild(terminal)
                    .orElseThrow(() -> {
                        throw new ReductionException("There is no production with [" + terminal + ", " + currentSymbol + "] symbols next to each other.");
                    });
        } while (table.isWeakPrecedence() ? currentNode.hasNonLeafChildOf(stackTop.unwrap()) : !precedenceLessThan(stackTop, fromStack));

        matchProduction(currentNode, symbolStack, output, rhs);
    }

    /**
     * Finds matching production, left-hand non-terminal is pushed on the symbol stack for further reduction. Production
     * is set to output in its concrete form (lexemes instead of terminals)
     *
     * @param node current node should point to the first symbol in found production rhs
     * @param symbolStack stack of symbols to parse
     * @param output productions output
     * @param rhs parsed right-hand side of production
     */
    private void matchProduction(TreeNode<Symbol> node, Deque<Symbol> symbolStack, List<Production> output, List<Symbol> rhs) {
        node.findClosestLeaf()
                .map(ProductionTreeNode.class::cast)
                .ifPresentOrElse(
                        leaf -> {
                            NonTerminal lhs = (NonTerminal)leaf.getValue();

                            symbolStack.push(lhs); // push reduced value back onto the stack

                            Production concreteProduction = getConcreteProduction(lhs, rhs, leaf.getProductionId());
                            output.add(concreteProduction);
                        },
                        () -> {
                            throw new ReductionException("Direct leaf couldn't be found for " + node.getValue());
                        });
    }

    /**
     * Builds concrete production from production tree leaf and list of right-hand side symbol obtained during parsing.
     * Concrete production is a production with exact lexemes instead of terminals.
     *
     * @param lhs left-hand side of found production
     * @param rhs right-hand side of found production with lexemes instead of terminals
     * @param productionId id of the production obtained from production tree, currently used only for debug purposes
     * @return
     */
    private Production getConcreteProduction(NonTerminal lhs, List<Symbol> rhs, int productionId) {
        if (log.isDebugEnabled()) {
            Production production = grammar.getProduction(productionId);
            log.debug("Production found: {} (number: {})", productionId, production);
        }

        Production concreteProduction = Production.of(lhs, rhs);
        log.debug("Production with lexemes included: {}", concreteProduction);
        return concreteProduction;
    }
}
