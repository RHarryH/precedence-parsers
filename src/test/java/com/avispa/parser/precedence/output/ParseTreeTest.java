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

package com.avispa.parser.precedence.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.ParserUtil;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.lexer.Lexeme;
import com.avispa.parser.precedence.parser.SyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.C;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
class ParseTreeTest {
    private static final ParseTree parseTree = new ParseTree(ParserUtil.getSampleParser());

    @Test
    void givenParser_whenParseEmptyString_thenEmptyTree() throws SyntaxException, LexerException {
        // when
        TreeNode<Symbol> tree = parseTree.parse("");

        // then
        assertNull(tree);
    }

    @Test
    void givenParser_whenNonEmptyInput_thenCorrectParseTree() throws SyntaxException, LexerException {
        // when
        TreeNode<Symbol> tree = parseTree.parse("aab");

        // then
        TreeNode<Symbol> expectedTree = new TreeNode<>(B);

        TreeNode<Symbol> CNode = new TreeNode<>(C);
        CNode.addChild(new TreeNode<>(Lexeme.of("b", b, 1)));

        TreeNode<Symbol> BNode = new TreeNode<>(B);
        BNode.addChild(new TreeNode<>(Lexeme.of("a", a, 1)));

        expectedTree.addChild(CNode);
        expectedTree.addChild(new TreeNode<>(Lexeme.of("a", a, 2)));
        expectedTree.addChild(BNode);

        assertEquals(expectedTree, tree);
    }
}