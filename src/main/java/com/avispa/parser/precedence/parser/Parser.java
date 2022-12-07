package com.avispa.parser.precedence.parser;


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
public abstract class Parser<O> {
    protected final Deque<Symbol> deque = new ArrayDeque<>();
    protected final OperatorGrammar grammar;
    private final Lexer lexer;

    protected Parser(String input, OperatorGrammar grammar) {
        input = "$" + input + "$";
        this.grammar = grammar;
        if(log.isDebugEnabled()) {
            log.debug("Grammar used for parsing: ");
            log.debug("{}", grammar);
        }

        this.lexer = new Lexer(input, grammar);
    }

    public List<O> parse() throws LexerException, SyntaxException {
        List<O> output = new ArrayList<>();
        
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

            log.debug("Current stack state: {}", deque);
        }

        log.trace("Output: {}", output);
        
        return output;
    }

    private void shift(Lexeme lexeme) throws LexerException {
        log.debug("SHIFT (< or = relation matched). Pushing {} on stack.", lexeme);
        deque.push(lexeme);
        lexer.getNext();
    }

    protected abstract void reduce(List<O> output) throws SyntaxException;
}
