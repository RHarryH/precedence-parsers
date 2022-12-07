package com.avispa.parser.precedence.parser;


import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.SimplePrecedenceGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SimplePrecedenceParser extends Parser<Production> {
    private final TreeNode<Symbol> productionsTree;

    public SimplePrecedenceParser(String input, SimplePrecedenceGrammar grammar) {
        super(input, grammar);

        this.productionsTree = ProductionsTreeBuilder.build(grammar.getProductions());
    }

    @Override
    protected void reduce(List<Production> output) throws SyntaxException {
        log.debug("REDUCE (> relation matched).");
        Symbol fromStack;
        Symbol stackTop;
        TreeNode<Symbol> currentNode = productionsTree;

        do {
            fromStack = deque.pop();
            stackTop = deque.peek();

            Symbol terminal = fromStack.unwrap();
            currentNode = currentNode.getChild(terminal)
                    .orElseThrow(() -> {
                        throw new ReductionException("Couldn't find child matching " + terminal + " terminal.");
                    });
        } while(!grammar.precedenceLessThan(stackTop, fromStack));

        currentNode.findClosestLeaf()
                .map(leaf -> (ProductionTreeNode<Symbol>)leaf)
                .ifPresentOrElse(leaf -> {
                    deque.push(leaf.getValue()); // push reduced value back onto the stack

                    int productionId = leaf.getProductionId();
                    Production production = grammar.getProduction(productionId);

                    log.debug("Production found: {} (number: {})", productionId + 1, production);
                    output.add(production);
                },
                () -> {throw new ReductionException("Direct leaf couldn't be found for ");});
    }
}
