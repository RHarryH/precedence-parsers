package com.avispa.parser.shuntingyard;

import com.avispa.parser.shuntingyard.token.FunctionToken;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
final class Call {
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
