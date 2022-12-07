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
            var node = new ProductionTreeNode<>(symbol, productionId);
            currentNode.addChild(node);
        });
    }
}
