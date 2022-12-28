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

package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.parser.SyntaxException;
import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.FunctionToken;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.token.UnaryOperatorToken;
import com.avispa.parser.token.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Rafał Hiszpański
 */
class ExpressionTreeTest {
    private static final ExpressionTree expressionTree = new ExpressionTree();

    @Test
    void givenNothing_whenParse_thenNull() throws SyntaxException, LexerException {
        assertNull(expressionTree.parse(""));
    }

    @Test
    void givenOperand_whenParse_thenCorrectExpressionTree() throws SyntaxException, LexerException {
        Operand two = Operand.from("2");
        TreeNode<Token> root = new TreeNode<>(two);

        assertEquals(root, expressionTree.parse("2"));
    }

    @Test
    void givenAddition_whenParse_thenCorrectExpressionTree() throws SyntaxException, LexerException {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.ADD);
        TreeNode<Token> operand1 = new TreeNode<>(Operand.from("2"));
        TreeNode<Token> operand2 = new TreeNode<>(Operand.from("3"));
        root.addChild(operand1);
        root.addChild(operand2);

        assertEquals(root, expressionTree.parse("2+3"));
    }

    @Test
    void givenSubtraction_whenParse_thenCorrectExpressionTree() throws SyntaxException, LexerException {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.SUBTRACT);
        TreeNode<Token> operand1 = new TreeNode<>(Operand.from("2"));
        TreeNode<Token> operand2 = new TreeNode<>(Operand.from("3"));
        root.addChild(operand1);
        root.addChild(operand2);

        assertEquals(root, expressionTree.parse("2-3"));
    }

    @Test
    void givenUnaryOperator_whenParse_thenCorrectExpressionTree() throws SyntaxException, LexerException {
        TreeNode<Token> root = new TreeNode<>(UnaryOperatorToken.PLUS);
        TreeNode<Token> operand1 = new TreeNode<>(Operand.from("2"));
        root.addChild(operand1);

        assertEquals(root, expressionTree.parse("+2"));
    }

    @Test
    void givenFunction_whenParse_thenCorrectExpressionTree() throws SyntaxException, LexerException {
        TreeNode<Token> root = new TreeNode<>(FunctionToken.MAX);
        TreeNode<Token> operand1 = new TreeNode<>(Operand.from("2"));
        TreeNode<Token> operand2 = new TreeNode<>(Operand.from("3"));
        root.addChild(operand1);
        root.addChild(operand2);

        assertEquals(root, expressionTree.parse("max(2, 3)"));
    }

    @Test
    void givenCombinedOperations_whenParse_thenCorrectExpressionTree() throws SyntaxException, LexerException {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.ADD);
        TreeNode<Token> operand1 = new TreeNode<>(Operand.from("2"));
        TreeNode<Token> operator1 = new TreeNode<>(BinaryOperatorToken.MULTIPLY);
        root.addChild(operand1);
        root.addChild(operator1);
        TreeNode<Token> operand11 = new TreeNode<>(Operand.from("3"));
        TreeNode<Token> operand12 = new TreeNode<>(Operand.from("4"));
        operator1.addChild(operand11);
        operator1.addChild(operand12);

        assertEquals(root, expressionTree.parse("2+3*4"));
    }
}