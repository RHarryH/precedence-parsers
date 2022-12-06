package com.avispa.parser.precedence.parser;


import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.grammar.OperatorGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.precedence.lexer.Lexeme;
import com.avispa.parser.precedence.lexer.Lexer;
import com.avispa.parser.precedence.lexer.LexerException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
public class Parser {
    private final Deque<Symbol> deque = new ArrayDeque<>();
    private final Lexer lexer;
    private final OperatorGrammar grammar;

    private final TreeNode<Symbol> productionsTree;

    public Parser(String input, OperatorGrammar grammar) {
        input = "$" + input + "$";
        this.grammar = grammar;
        this.lexer = new Lexer(input, grammar);

        this.productionsTree = ProductionsTreeBuilder.build(grammar.getProductions());
    }

    public List<Symbol> parse() throws LexerException, SyntaxException {
        List<Symbol> output = new ArrayList<>();
        
        while(lexer.hasCharactersLeft()) {
            Symbol stackTop = deque.peek();
            Lexeme nextLexeme = lexer.peekNext();
            Terminal operator = nextLexeme.getTerminal();
            log.trace("Stack top: {}, next lexeme: {}", stackTop, nextLexeme);

            if(null == stackTop || grammar.precedenceLessThan(stackTop, operator) || grammar.precedenceEquals(stackTop, operator)) {
                shift(nextLexeme);
            } else if(grammar.precedenceGreaterThan(stackTop, operator)){
                reduce(output);
            } else {
                throw new SyntaxException(nextLexeme.getValue());
            }

            log.trace("Stack state: {}", deque);
        }

        log.debug("Output: {}", output);
        
        return output;
    }

    private void shift(Symbol operator) throws LexerException {
        log.debug("SHIFT (< or = relation matched). Pushing {} on stack.", operator);
        deque.push(operator);
        lexer.getNext();
    }

    private void reduce(List<Symbol> result) {
        log.debug("REDUCE (> relation matched).");
        Symbol fromStack;
        Symbol stackTop;

        do {
            fromStack = deque.pop();
            stackTop = deque.peek();

            log.info("Adding {} to the output", fromStack);
            result.add(fromStack);
        } while(grammar.precedenceLessThan(fromStack, stackTop));
    }
}
