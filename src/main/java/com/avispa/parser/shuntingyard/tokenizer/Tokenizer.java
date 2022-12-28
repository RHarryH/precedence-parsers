/*
 * MIT License
 *
 * Copyright (c) 2022 Rafał Hiszpański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.avispa.parser.shuntingyard.tokenizer;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.FunctionToken;
import com.avispa.parser.shuntingyard.token.Misc;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.token.UnaryOperatorToken;
import com.avispa.parser.token.Token;
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

    public List<Token> tokenize(String expression) throws LexerException {
        List<Token> tokens = new ArrayList<>();
        
        expression = expression.replaceAll("\\s+","");

        log.debug("Expression: \"{}\"", expression);

        while(!expression.isEmpty()) {
            boolean tokenFound = searchFunctions(expression, tokens) ||
                                 searchUnaryOperators(expression, tokens) ||
                                 searchBinaryOperators(expression, tokens) ||
                                 searchMisc(expression, tokens) ||
                                 searchOperands(expression, tokens);

            if(tokenFound) {
                expression = expression.substring(tokens.get(tokens.size() - 1).getValue().length());
            } else {
                throw new LexerException("Unknown token starting from \"" + expression + "\"");
            }
        }
        
        return tokens;
    }

    private boolean searchFunctions(String expression, List<Token> tokens) {
        return searchEnumSymbols(FunctionToken.class, expression, tokens);
    }

    private boolean searchUnaryOperators(String expression, List<Token> tokens) {
        Token foundToken = searchToken(UnaryOperatorToken.class, expression);

        if(null != foundToken && isUnaryOperator(tokens)) {
            tokens.add(foundToken);
            return true;
        }

        return false;
    }

    private boolean isUnaryOperator(List<Token> tokens) {
        if(!tokens.isEmpty()) {
            Token lastToken = tokens.get(tokens.size() -1);
            return lastToken instanceof BinaryOperatorToken || lastToken.equals(Misc.LEFT_PARENTHESIS);
        }

        return true;
    }

    private boolean searchBinaryOperators(String expression, List<Token> tokens) {
        return searchEnumSymbols(BinaryOperatorToken.class, expression, tokens);
    }

    private boolean searchMisc(String expression, List<Token> tokens) {
        return searchEnumSymbols(Misc.class, expression, tokens);
    }

    private <E extends Enum<E> & Token> boolean searchEnumSymbols(Class<E> enumToken, String expression, List<Token> tokens) {
        Token foundToken = searchToken(enumToken, expression);

        if(null != foundToken) {
            tokens.add(foundToken);
            return true;
        }

        return false;
    }

    private <E extends Enum<E> & Token> Token searchToken(Class<E> enumToken, String expression) {
        Token foundToken = null;
        for(E token : enumToken.getEnumConstants()) {
            if(expression.startsWith(token.getValue())) {
                foundToken = token;
                break;
            }
        }
        return foundToken;
    }

    private boolean searchOperands(String expression, List<Token> tokens) {
        Matcher matcher = operandPattern.matcher(expression);

        if(matcher.find()) {
            tokens.add(Operand.from(matcher.group()));
            return true;
        }

        return false;
    }
}
