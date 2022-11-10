package com.avispa.precedence_parsers.shunting_yard;

import com.avispa.precedence_parsers.shunting_yard.token.Function;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public class Call {
    private final Function function;
    private int argCount;

    public Call(Function function) {
        this.function = function;
        this.argCount = 0;
    }

    public void incArgumentCount() {
        argCount++;
    }

    public boolean hasAllArguments() {
        return function.getExpectedArgCount() == argCount;
    }
}
