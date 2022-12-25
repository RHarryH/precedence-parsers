package com.avispa.parser.precedence.parser;


import com.avispa.parser.Parser;
import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.lexer.Lexeme;
import com.avispa.parser.precedence.lexer.Lexer;
import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
public abstract class PrecedenceParser<O> implements Parser<O> {
    protected final Grammar grammar;

    private final PrecedenceTable table;
    private final PrecedenceFunctions functions;

    protected PrecedenceParser(Grammar grammar, PrecedenceTable table, PrecedenceFunctions functions) {
        this.grammar = grammar;
        this.table = table;
        this.functions = functions;

        if(log.isDebugEnabled()) {
            log.debug("Grammar used for parsing: ");
            log.debug("{}", grammar);
        }
    }

    @Override
    public List<O> parse(String input) throws LexerException, SyntaxException {
        input = "$" + input + "$"; // add markers

        Deque<Symbol> symbolStack = new ArrayDeque<>();
        Lexer lexer = new Lexer(input, grammar);

        List<O> output = new ArrayList<>();
        
        while(lexer.hasCharactersLeft()) {
            Symbol stackTop = symbolStack.peek();
            Lexeme nextLexeme = lexer.peekNext();

            log.trace("Stack top: {}, next lexeme: {}", stackTop, nextLexeme);

            if(null == stackTop || precedenceLessThan(stackTop, nextLexeme) || precedenceEquals(stackTop, nextLexeme)) {
                shift(lexer, symbolStack);
            } else if(precedenceGreaterThan(stackTop, nextLexeme)){
                reduce(output, symbolStack);
            } else {
                throw new SyntaxException("Syntax error at the vicinity of: " + nextLexeme.getValue());
            }

            log.debug("Current stack state: {}", symbolStack);
        }

        log.trace("Output: {}", output);
        
        return output;
    }

    private void shift(Lexer lexer, Deque<Symbol> symbolStack) throws LexerException {
        Lexeme lexeme = lexer.getNext();
        log.debug("SHIFT (< or = relation matched). Pushing {} on stack.", lexeme);
        symbolStack.push(lexeme);
    }

    protected abstract void reduce(List<O> output, Deque<Symbol> symbolStack) throws SyntaxException;

    protected boolean precedenceLessThan(Symbol a, Symbol b) {
        a = a.unwrap();
        b = b.unwrap();

        if(null != functions) {
            int fA = functions.getFFor(a);
            int gB = functions.getGFor(b);

            if(log.isDebugEnabled()) {
                log.debug("Precedence check: f({}) < g({}), is: {} {} {}", a, b, fA, getRelationCharacter(fA, gB), gB);
            }

            return fA < gB;
        } else {
            Precedence precedence = getPrecedence(a, b, Precedence.LESS_THAN);

            return Precedence.LESS_THAN.equals(precedence) || Precedence.LESS_THAN_OR_EQUALS.equals(precedence);
        }
    }

    protected boolean precedenceGreaterThan(Symbol a, Symbol b) {
        a = a.unwrap();
        b = b.unwrap();

        if(null != functions) {
            int fA = functions.getFFor(a);
            int gB = functions.getGFor(b);

            if(log.isDebugEnabled()) {
                log.debug("Precedence check: f({}) > g({}), is: {} {} {}", a, b, fA, getRelationCharacter(fA, gB), gB);
            }

            return fA > gB;
        } else {
            Precedence precedence = getPrecedence(a, b, Precedence.GREATER_THAN);

            return Precedence.GREATER_THAN.equals(precedence);
        }
    }

    protected boolean precedenceEquals(Symbol a, Symbol b) {
        a = a.unwrap();
        b = b.unwrap();

        if(null != functions) {
            int fA = functions.getFFor(a);
            int gB = functions.getGFor(b);

            if(log.isDebugEnabled()) {
                log.debug("Precedence check: f({}) = g({}), is: {} {} {}", a, b, fA, getRelationCharacter(fA, gB), gB);
            }
            return fA == gB;
        } else {
            Precedence precedence = getPrecedence(a, b, Precedence.EQUALS);

            return Precedence.EQUALS.equals(precedence) || Precedence.LESS_THAN_OR_EQUALS.equals(precedence);
        }
    }

    private Precedence getPrecedence(Symbol a, Symbol b, Precedence expected) {
        Precedence precedence = table.get(a, b);

        if(null == precedence) {
            log.warn("Precedence not found");
        } else {
            log.debug("Precedence check: {} {} {}, is: {} {} {}", a, expected, b, a, precedence, b);
        }

        return precedence;
    }

    private char getRelationCharacter(int a, int b) {
        if(a < b) {
            return '<';
        } else if(a > b) {
            return '>';
        } else {
            return '=';
        }
    }
}
