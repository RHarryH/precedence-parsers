package com.avispa.parser.precedence.parser;


import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

@Slf4j
public class SimplePrecedenceParser extends PrecedenceParser<Production> {
    private final TreeNode<Symbol> productionsTree;

    SimplePrecedenceParser(Grammar grammar, PrecedenceTable table) {
        this(grammar, table, null);
    }

    SimplePrecedenceParser(Grammar grammar, PrecedenceTable table, PrecedenceFunctions functions) {
        super(grammar, table, functions);

        this.productionsTree = ProductionsTreeBuilder.build(grammar.getProductions());
    }

    @Override
    protected void reduce(List<Production> output, Deque<Symbol> deque) throws SyntaxException {
        log.debug("REDUCE (> relation matched).");
        try {
            doReduce(output, deque);
        } catch (ReductionException e){
            throw new SyntaxException("No matching production: " + e.getMessage());
        }
    }

    private void doReduce(List<Production> output, Deque<Symbol> deque) {
        Symbol fromStack;
        Symbol stackTop;
        TreeNode<Symbol> currentNode = productionsTree;
        List<Symbol> rhs = new ArrayList<>();

        do {
            fromStack = deque.pop();
            stackTop = deque.peek();

            rhs.add(fromStack);

            Symbol terminal = fromStack.unwrap();
            Symbol currentSymbol = currentNode.getValue();

            currentNode = currentNode.getChild(terminal)
                    .orElseThrow(() -> {
                        throw new ReductionException("There is no production with [" + terminal + ", " + currentSymbol + "] symbols next to each other.");
                    });
        } while (!precedenceLessThan(stackTop, fromStack));

        while(currentNode.getChild(deque.peek().unwrap()).isPresent()) {
            rhs.add(deque.peek());
            currentNode = currentNode.getChild(deque.pop().unwrap()).get();
        }

        currentNode.findClosestLeaf()
                .map(ProductionTreeNode.class::cast)
                .ifPresentOrElse(leaf -> {
                            deque.push(leaf.getValue()); // push reduced value back onto the stack

                            if (log.isDebugEnabled()) {
                                int productionId = leaf.getProductionId();
                                Production production = grammar.getProduction(productionId);
                                log.debug("Production found: {} (number: {})", productionId, production);
                            }

                            Collections.reverse(rhs);
                            Production concreteProduction = Production.of((NonTerminal) leaf.getValue(), rhs);
                            log.debug("Production with lexemes included: {}", concreteProduction);

                            output.add(concreteProduction);
                        },
                        () -> {
                            throw new ReductionException("Direct leaf couldn't be found for ");
                        });
    }
}
