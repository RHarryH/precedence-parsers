package com.avispa.precedence_parsers.shunting_yard;

import com.avispa.precedence_parsers.shunting_yard.token.FunctionToken;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public class Call {
    private final FunctionToken functionToken;
    private int argCount;

    public Call(FunctionToken functionToken) {
        this.functionToken = functionToken;
        this.argCount = 0;
    }

    public void incArgumentCount() {
        argCount++;
    }

    public boolean hasAllArguments() {
        return functionToken.getExpectedArgCount() == argCount;
    }
}
