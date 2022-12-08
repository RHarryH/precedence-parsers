package com.avispa.parser.precedence.parser;

import com.avispa.parser.precedence.grammar.OperatorPrecedenceGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.List;

@Slf4j
public class OperatorPrecedenceParser extends PrecedenceParser<Symbol> {
    public OperatorPrecedenceParser(OperatorPrecedenceGrammar grammar) {
        super(grammar);
    }

    @Override
    protected void reduce(List<Symbol> output, Deque<Symbol> deque) throws SyntaxException {
        log.debug("REDUCE (> relation matched).");
        Symbol fromStack;
        Symbol stackTop;

        do {
            fromStack = deque.pop();
            stackTop = deque.peek();

            log.info("Adding {} to the output", fromStack);
            output.add(fromStack);
        } while(!grammar.precedenceLessThan(stackTop, fromStack));
    }
}
