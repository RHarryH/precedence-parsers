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
import com.avispa.parser.misc.tree.TreePrinter;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ProductionsTreeBuilder {

    static TreeNode<Symbol> build(List<Production> productions) {
        final TreeNode<Symbol> productionsTree = new TreeNode<>(null);

        for (int productionId = 0; productionId < productions.size(); productionId++) {
            Production production = productions.get(productionId);

            var currentNode = productionsTree;
            var symbols = production.getRhs();
            var it = symbols.listIterator(symbols.size());

            while (it.hasPrevious()) {
                currentNode = addNode(currentNode, it.previous());
            }
            addProductionNode(currentNode, production.getLhs(), productionId);
        }

        if(log.isDebugEnabled()) {
            log.info(TreePrinter.print(productionsTree));
        }

        return productionsTree;
    }

    private static TreeNode<Symbol> addNode(final TreeNode<Symbol> currentNode, Symbol symbol) {
        return currentNode.getChild(symbol).orElseGet(() -> {
            var newNode = new TreeNode<>(symbol);
            currentNode.addChild(newNode);
            return newNode;
        });
    }

    private static void addProductionNode(final TreeNode<Symbol> currentNode, Symbol symbol, int productionId) {
        currentNode.getChild(symbol).ifPresentOrElse(node -> {}, () -> {
            var node = new ProductionTreeNode(symbol, productionId);
            currentNode.addChild(node);
        });
    }
}
