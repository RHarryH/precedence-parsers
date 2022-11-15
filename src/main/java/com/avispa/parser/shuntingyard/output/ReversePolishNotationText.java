package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.shuntingyard.IParser;
import com.avispa.parser.token.Token;

import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
public final class ReversePolishNotationText extends AbstractOutputTransformer<String> {
    public ReversePolishNotationText() {
        super();
    }

    public ReversePolishNotationText(IParser parser) {
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
