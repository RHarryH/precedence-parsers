package com.avispa.parser.precedence.parser;


import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.SimplePrecedenceGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

@Slf4j
public class SimplePrecedenceParser extends PrecedenceParser<Production> {
    private final TreeNode<Symbol> productionsTree;

    public SimplePrecedenceParser(SimplePrecedenceGrammar grammar) {
        super(grammar);

        this.productionsTree = ProductionsTreeBuilder.build(grammar.getProductions());
    }

    @Override
    protected void reduce(List<Production> output, Deque<Symbol> deque) throws SyntaxException {
        log.debug("REDUCE (> relation matched).");
        Symbol fromStack;
        Symbol stackTop;
        TreeNode<Symbol> currentNode = productionsTree;
        List<Symbol> rhs = new ArrayList<>();

        do {
            fromStack = deque.pop();
            stackTop = deque.peek();

            rhs.add(fromStack);

            Symbol terminal = fromStack.unwrap();
            currentNode = currentNode.getChild(terminal)
                    .orElseThrow(() -> {
                        throw new ReductionException("Couldn't find child matching " + terminal + " terminal.");
                    });
        } while(!grammar.precedenceLessThan(stackTop, fromStack));

        currentNode.findClosestLeaf()
                .map(ProductionTreeNode.class::cast)
                .ifPresentOrElse(leaf -> {
                    deque.push(leaf.getValue()); // push reduced value back onto the stack

                    if(log.isDebugEnabled()) {
                        int productionId = leaf.getProductionId();
                        Production production = grammar.getProduction(productionId);
                        log.debug("Production found: {} (number: {})", productionId, production);
                    }

                    Collections.reverse(rhs);
                    Production concreteProduction = Production.of((NonTerminal) leaf.getValue(), rhs);
                    log.debug("Production with lexemes included: {}", concreteProduction);

                    output.add(concreteProduction);
                },
                () -> {throw new ReductionException("Direct leaf couldn't be found for ");});
    }
}
