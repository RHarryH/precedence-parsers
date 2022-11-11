package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.shuntingyard.IParser;
import com.avispa.parser.shuntingyard.token.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
public class ListReversePolishNotation extends AbstractOutputTransformer<List<String>> {
    public ListReversePolishNotation() {
        super();
    }

    public ListReversePolishNotation(IParser parser) {
        super(parser);
    }

    @Override
    public List<String> parse(String expression) {
        return getParser()
                .parse(expression)
                .stream()
                .map(Token::getValue)
                .collect(Collectors.toList());
    }
}
