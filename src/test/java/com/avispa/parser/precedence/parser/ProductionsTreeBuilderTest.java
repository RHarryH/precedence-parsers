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
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Symbol;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.avispa.parser.precedence.TestSymbols.add;
import static com.avispa.parser.precedence.TestSymbols.expression;
import static com.avispa.parser.precedence.TestSymbols.factor;
import static com.avispa.parser.precedence.TestSymbols.lpar;
import static com.avispa.parser.precedence.TestSymbols.marker;
import static com.avispa.parser.precedence.TestSymbols.mul;
import static com.avispa.parser.precedence.TestSymbols.number;
import static com.avispa.parser.precedence.TestSymbols.rpar;
import static com.avispa.parser.precedence.TestSymbols.start;
import static com.avispa.parser.precedence.TestSymbols.term;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class ProductionsTreeBuilderTest {
    @Test
    void givenOperatorPrecedenceGrammar_whenBuildTree_thenCorrect() throws IncorrectGrammarException, IOException {
        // given
        ContextFreeGrammar grammar = ContextFreeGrammar.fromWithBoundaryMarker(new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt"), expression);

        TreeNode<Symbol> expectedTree = createExpectedTree();

        // when
        var productionsTree = ProductionsTreeBuilder.build(grammar.getProductions());

        // then
        assertEquals(expectedTree, productionsTree);
    }

    private TreeNode<Symbol> createExpectedTree() {
        TreeNode<Symbol> expectedTree = new TreeNode<>(null);
        startBranch(expectedTree);
        termBranch(expectedTree);
        factorBranch(expectedTree);
        numberBranch(expectedTree);
        parenthesisBranch(expectedTree);
        return expectedTree;
    }

    private void startBranch(TreeNode<Symbol> expectedTree) {
        TreeNode<Symbol> markerNode1 = new TreeNode<>(marker);
        expectedTree.addChild(markerNode1);
        TreeNode<Symbol> expressionNode = new TreeNode<>(expression);
        markerNode1.addChild(expressionNode);
        TreeNode<Symbol> markerNode2 = new TreeNode<>(marker);
        expressionNode.addChild(markerNode2);
        markerNode2.addChild(new ProductionTreeNode(start, 0));
    }

    private void termBranch(TreeNode<Symbol> expectedTree) {
        TreeNode<Symbol> termNode = new TreeNode<>(term);
        expectedTree.addChild(termNode);
        TreeNode<Symbol> addNode = new TreeNode<>(add);
        termNode.addChild(addNode);
        TreeNode<Symbol> expressionNode = new TreeNode<>(expression);
        addNode.addChild(expressionNode);
        expressionNode.addChild(new ProductionTreeNode(expression, 1));

        termNode.addChild(new ProductionTreeNode(expression, 2));
    }

    private void factorBranch(TreeNode<Symbol> expectedTree) {
        TreeNode<Symbol> factorNode = new TreeNode<>(factor);
        expectedTree.addChild(factorNode);
        TreeNode<Symbol> mulNode = new TreeNode<>(mul);
        factorNode.addChild(mulNode);
        TreeNode<Symbol> termNode = new TreeNode<>(term);
        mulNode.addChild(termNode);
        termNode.addChild(new ProductionTreeNode(term, 3));

        factorNode.addChild(new ProductionTreeNode(term, 4));
    }

    private void numberBranch(TreeNode<Symbol> expectedTree) {
        TreeNode<Symbol> numberNode = new TreeNode<>(number);
        expectedTree.addChild(numberNode);
        numberNode.addChild(new ProductionTreeNode(factor, 5));
    }

    private void parenthesisBranch(TreeNode<Symbol> expectedTree) {
        TreeNode<Symbol> rparNode = new TreeNode<>(rpar);
        expectedTree.addChild(rparNode);
        TreeNode<Symbol> expressionNode = new TreeNode<>(expression);
        rparNode.addChild(expressionNode);
        TreeNode<Symbol> lparNode = new TreeNode<>(lpar);
        expressionNode.addChild(lparNode);
        lparNode.addChild(new ProductionTreeNode(factor, 6));
    }

}