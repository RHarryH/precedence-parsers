package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.output.AbstractOutputTransformer;
import com.avispa.parser.precedence.parser.SyntaxException;
import com.avispa.parser.shuntingyard.ShuntingYard;
import com.avispa.parser.token.Token;

import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
public final class ReversePolishNotationText extends AbstractOutputTransformer<String, Token> {
    public ReversePolishNotationText() {
        super(new ShuntingYard());
    }

    @Override
    public String parse(String expression) throws SyntaxException, LexerException {
        return getParser()
                .parse(expression)
                .stream()
                .map(Token::getValue)
                .collect(Collectors.joining(" "));
    }
}
