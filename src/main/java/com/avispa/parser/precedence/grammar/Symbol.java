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

package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.lexer.Lexeme;
import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
public abstract class Symbol {
    protected String name;

    protected Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * If symbol is a lexeme, unwrap it to get actual terminal symbol
     * @return
     */
    public Symbol unwrap() {
        if(this instanceof Lexeme) {
            return ((Lexeme) this).getTerminal();
        }
        return this;
    }

    @Override
    public String toString() {
        return name;
    }
}
