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

package com.avispa.parser.precedence.lexer;

import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class Lexeme extends Symbol {
    private final String value;
    private final int index;
    private final Terminal terminal;

    public static Lexeme of(String value, Terminal terminal, int index) {
        if(index < 1) {
            throw new IllegalArgumentException("Index must be greater than 0");
        }
        return new Lexeme(value, terminal, index);
    }

    private Lexeme(String value, Terminal terminal, int index) {
        super(terminal.getName());
        this.index = index;
        this.value = value;
        this.terminal = terminal;
    }

    public int getValueLength() {
        return value.length();
    }

    @Override
    public String toString() {
        return super.toString() + "_" + index + ":" + value;
    }
}
