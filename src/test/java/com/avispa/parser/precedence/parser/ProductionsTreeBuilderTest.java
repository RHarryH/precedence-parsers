package com.avispa.parser.precedence.parser;

import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.OperatorGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import org.junit.jupiter.api.Test;

import static com.avispa.parser.precedence.TokenUtil.add;
import static com.avispa.parser.precedence.TokenUtil.expression;
import static com.avispa.parser.precedence.TokenUtil.factor;
import static com.avispa.parser.precedence.TokenUtil.lpar;
import static com.avispa.parser.precedence.TokenUtil.mul;
import static com.avispa.parser.precedence.TokenUtil.number;
import static com.avispa.parser.precedence.TokenUtil.rpar;
import static com.avispa.parser.precedence.TokenUtil.start;
import static com.avispa.parser.precedence.TokenUtil.term;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class ProductionsTreeBuilderTest {
    @Test
    void givenOperatorPrecedenceGrammar_whenBuildTree_thenCorrect() throws IncorrectGrammarException {
        // given
        OperatorGrammar grammar = new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt").readOperatorPrecedence();

        TreeNode<Symbol> expectedTree = createExpectedTree();

        // when
        var productionsTree = ProductionsTreeBuilder.build(grammar.getProductions());

        // then
        assertEquals(expectedTree, productionsTree);
    }

    private TreeNode<Symbol> createExpectedTree() {
        TreeNode<Symbol> expectedTree = new TreeNode<>(null);
        expressionBranch(expectedTree);
        termBranch(expectedTree);
        factorBranch(expectedTree);
        numberBranch(expectedTree);
        parenthesisBranch(expectedTree);
        return expectedTree;
    }

    private void expressionBranch(TreeNode<Symbol> expectedTree) {
        TreeNode<Symbol> expressionNode = new TreeNode<>(expression);
        expectedTree.addChild(expressionNode);
        expressionNode.addChild(new ProductionTreeNode(start, 0));
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