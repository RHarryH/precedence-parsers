package com.avispa.parser.precedence.parser;

import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.List;

@Slf4j
public class OperatorPrecedenceParser extends PrecedenceParser<Symbol> {
    OperatorPrecedenceParser(Grammar grammar, PrecedenceTable table) {
        super(grammar, table, null);
    }

    OperatorPrecedenceParser(Grammar grammar, PrecedenceTable table, PrecedenceFunctions functions) {
        super(grammar, table, functions);
    }

    @Override
    protected void reduce(List<Symbol> output, Deque<Symbol> symbolStack) throws SyntaxException {
        log.debug("REDUCE (> relation matched).");
        Symbol fromStack;
        Symbol stackTop;

        do {
            fromStack = symbolStack.pop();
            stackTop = symbolStack.peek();

            log.info("Adding {} to the output", fromStack);
            output.add(fromStack);
        } while(!precedenceLessThan(stackTop, fromStack));
    }
}
