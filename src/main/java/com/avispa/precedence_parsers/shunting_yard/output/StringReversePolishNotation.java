package com.avispa.precedence_parsers.shunting_yard.output;

import com.avispa.precedence_parsers.shunting_yard.IParser;
import com.avispa.precedence_parsers.shunting_yard.token.Token;

import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
public class StringReversePolishNotation extends AbstractOutputTransformer<String> {
    public StringReversePolishNotation() {
        super();
    }

    public StringReversePolishNotation(IParser parser) {
        super(parser);
    }

    @Override
    public String parse(String expression) {
        return getParser()
                .parse(expression)
                .stream()
                .map(Token::getValue)
                .collect(Collectors.joining(" "));
    }
}
