package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.shuntingyard.Parser;
import com.avispa.parser.token.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
public final class ReversePolishNotationList extends AbstractOutputTransformer<List<String>> {
    public ReversePolishNotationList() {
        super();
    }

    public ReversePolishNotationList(Parser parser) {
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
