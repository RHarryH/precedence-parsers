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

        for(Production production : productions) {
            var currentNode = productionsTree;
            var symbols = production.getRhs();
            var it = symbols.listIterator(symbols.size());

            while(it.hasPrevious()) {
                currentNode = addNode(currentNode, it.previous());
            }
            addNode(currentNode, production.getLhs());
        }

        if(log.isDebugEnabled()) {
            log.info(TreePrinter.print(productionsTree));
        }

        return productionsTree;
    }

    private static TreeNode<Symbol> addNode(TreeNode<Symbol> currentNode, Symbol symbol) {
        var node = currentNode.getChild(symbol);
        if(null == node) {
            node = new TreeNode<>(symbol);
            currentNode.addChild(node);
        }
        currentNode = node;
        return currentNode;
    }
}
