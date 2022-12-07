package com.avispa.parser.precedence.parser;


import com.avispa.parser.precedence.grammar.SimplePrecedenceGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class OperatorPrecedenceParser extends Parser<Symbol> {
    public OperatorPrecedenceParser(String input, SimplePrecedenceGrammar grammar) {
        super(input, grammar);
    }

    @Override
    protected void reduce(List<Symbol> output) throws SyntaxException {
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
