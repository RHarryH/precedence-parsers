package com.avispa.precedence_parsers.shunting_yard.tokenizer;

import com.avispa.precedence_parsers.shunting_yard.token.BinaryOperator;
import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operand;
import com.avispa.precedence_parsers.shunting_yard.token.Token;
import com.avispa.precedence_parsers.shunting_yard.token.UnaryOperator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class Tokenizer {
    private static final Pattern operandPattern = Pattern.compile("^((\\d+(\\.\\d*)?)|(\\.\\d+))");

    public List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();
        
        expression = expression.replaceAll("\\s+","");

        log.debug("Expression: \"{}\"", expression);

        while(!expression.isEmpty()) {
            boolean tokenFound = searchUnaryOperators(expression, tokens) ||
                                 searchBinaryOperators(expression, tokens) ||
                                 searchMisc(expression, tokens) ||
                                 searchOperands(expression, tokens);

            if(tokenFound) {
                expression = expression.substring(tokens.get(tokens.size() - 1).getValue().length());
            } else {
                throw new IllegalStateException("Unknown token starting from \"" + expression + "\"");
            }
        }
        
        return tokens;
    }

    private boolean searchUnaryOperators(String expression, List<Token> tokens) {
        Token foundToken = null;
        for(UnaryOperator token : UnaryOperator.values()) {
            if(expression.startsWith(token.getValue())) {
                foundToken = token;
                break;
            }
        }

        if(null != foundToken && isUnaryOperator(tokens)) {
            tokens.add(foundToken);
            return true;
        }

        return false;
    }

    private boolean isUnaryOperator(List<Token> tokens) {
        if(!tokens.isEmpty()) {
            Token lastToken = tokens.get(tokens.size() -1);
            return lastToken instanceof BinaryOperator || lastToken.equals(Misc.LEFT_PARENTHESIS);
        }

        return true;
    }

    private boolean searchBinaryOperators(String expression, List<Token> tokens) {
        return searchEnumSymbols(BinaryOperator.class, expression, tokens);
    }

    private boolean searchMisc(String expression, List<Token> tokens) {
        return searchEnumSymbols(Misc.class, expression, tokens);
    }

    private <E extends Enum<E> & Token> boolean searchEnumSymbols(Class<E> enumToken, String expression, List<Token> tokens) {
        Token foundToken = null;
        for(E token : enumToken.getEnumConstants()) {
            if(expression.startsWith(token.getValue())) {
                foundToken = token;
                break;
            }
        }

        if(null != foundToken) {
            tokens.add(foundToken);
            return true;
        }

        return false;
    }

    private boolean searchOperands(String expression, List<Token> tokens) {
        Matcher matcher = operandPattern.matcher(expression);

        if(matcher.find()) {
            tokens.add(new Operand(matcher.group()));
            return true;
        }

        return false;
    }
}
