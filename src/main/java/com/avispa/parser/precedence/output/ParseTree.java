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
import com.avispa.parser.output.AbstractOutputTransformer;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.parser.PrecedenceParser;
import com.avispa.parser.precedence.parser.SyntaxException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public final class ParseTree extends AbstractOutputTransformer<TreeNode<Symbol>, Production> {

    public ParseTree(PrecedenceParser<Production> parser) {
        super(parser);
    }

    @Override
    public TreeNode<Symbol> parse(String input) throws SyntaxException, LexerException {
        Deque<Production> productions = new ArrayDeque<>(getParser().parse(input));

        if(productions.isEmpty()) {
            return null;
        }

        TreeNode<Symbol> root = new TreeNode<>(productions.peekLast().getLhs());
        buildParseTree(root, productions);

        return root;
    }

    private void buildParseTree(TreeNode<Symbol> currentNode, Deque<Production> productions) {
        Production currentProduction = productions.removeLast(); // get last production on the stack, this is the first derivation from start symbol
        List<Symbol> currentRhs = currentProduction.getRhs();
        var it = currentRhs.listIterator(currentRhs.size());

        // iterate productions backwards, this is because how parser produces the list of productions
        while(it.hasPrevious()) {
            Symbol symbol = it.previous();
            var node = new TreeNode<>(symbol); // create new node

            if(symbol instanceof NonTerminal) { // for non-terminals we want to expand production
                Production production = productions.peekLast();
                if (production != null && symbol.equals(production.getLhs())) { // if production
                    buildParseTree(node, productions);
                }
            }

            currentNode.addChild(node);
        }
    }
}
