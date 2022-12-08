package com.avispa.parser.precedence.parser;


import com.avispa.parser.Parser;
import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.grammar.OperatorGrammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.lexer.Lexeme;
import com.avispa.parser.precedence.lexer.Lexer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
public abstract class PrecedenceParser<O> implements Parser<O> {
    protected final OperatorGrammar grammar;

    protected PrecedenceParser(OperatorGrammar grammar) {
        this.grammar = grammar;
        if(log.isDebugEnabled()) {
            log.debug("Grammar used for parsing: ");
            log.debug("{}", grammar);
        }
    }

    @Override
    public List<O> parse(String input) throws LexerException, SyntaxException {
        input = "$" + input + "$"; // add markers

        Deque<Symbol> deque = new ArrayDeque<>();
        Lexer lexer = new Lexer(input, grammar);

        List<O> output = new ArrayList<>();
        
        while(lexer.hasCharactersLeft()) {
            Symbol stackTop = deque.peek();
            Lexeme nextLexeme = lexer.peekNext();

            log.trace("Stack top: {}, next lexeme: {}", stackTop, nextLexeme);

            if(null == stackTop || grammar.precedenceLessThan(stackTop, nextLexeme) || grammar.precedenceEquals(stackTop, nextLexeme)) {
                shift(lexer, deque);
            } else if(grammar.precedenceGreaterThan(stackTop, nextLexeme)){
                reduce(output, deque);
            } else {
                throw new SyntaxException(nextLexeme.getValue());
            }

            log.debug("Current stack state: {}", deque);
        }

        log.trace("Output: {}", output);
        
        return output;
    }

    private void shift(Lexer lexer, Deque<Symbol> deque) throws LexerException {
        Lexeme lexeme = lexer.getNext();
        log.debug("SHIFT (< or = relation matched). Pushing {} on stack.", lexeme);
        deque.push(lexeme);
    }

    protected abstract void reduce(List<O> output, Deque<Symbol> deque) throws SyntaxException;
}
