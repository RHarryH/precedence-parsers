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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class GrammarFile {
    private final Pattern grammar = Pattern.compile("grammar *(\\w+) *;");

    private final Pattern terminalPattern = Pattern.compile("[A-Z]\\w+");
    private final Pattern nonTerminalPattern = Pattern.compile("[a-z]\\w+");

    // optionally [^\S\r\n] instead of space for any whitespace characters except new line characters
    /**
     * Accepts any line starting with valid terminal name (uppercase then any alphanumeric + _) followed by colon and
     * any number of characters except the new line and tabulation characters, ended with semicolon. Both left and right
     * sides of the colon might be surrounded by spaces.
     * Example: "ABC: abc;".
     */
    private final Pattern lexerLinePattern = Pattern.compile("^[A-Z]\\w+ *: *[^\\r\\n\\t]+ *;$");

    /**
     * Accepts any line starting with valid non-terminal name (lowercase then any alphanumeric + _) followed by colon and
     * any strings with only uppercase or only lowercase letters representing terminal or non-terminal symbols respectively.
     * Each symbol might be surrounded by spaces. Max number of allowed symbols is 10. Alternative symbol "|" is also allowed to
     * provide multiple productions with the same left-hand side symbol. Number of alternatives is also limited to 10.
     * Correct examples:
     *     - "abc: ABC abc;"
     *     - "a: A cc | dx;"
     * Incorrect example: "abc: aaaBB;"
     */
    private final Pattern parserLinePattern = Pattern.compile("^[a-z]\\w+: *(?:[A-Z]\\w+|[a-z]\\w+) *(?: +(?:[A-Z]\\w+|[a-z]\\w+) *){0,9}(?:\\| *(?:[A-Z]\\w+|[a-z]\\w+) *(?: +(?:[A-Z]\\w+|[a-z]\\w+) *){0,9}){0,9};$");

    private final Map<String, Terminal> terminals = new HashMap<>();
    @Getter(AccessLevel.MODULE)
    private final List<Production> productions = new ArrayList<>();
    @Getter(AccessLevel.MODULE)
    private String name;

    public GrammarFile(String fileName) throws IOException {
        parseFile(fileName);
    }

    private void parseFile(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(this::parseLine);
        }
    }

    private void parseLine(String line) {
        if(line.isBlank() || line.startsWith("//")) { // if blank or comment then ignore
            return;
        }

        if(!processGrammarLine(line) && !processLexerLine(line) && !processParserLine(line)) {
            log.warn("Line can't be parsed. It will be ignored. Line: {}", line);
        }
    }

    private boolean processGrammarLine(String line) {
        Matcher matcher = grammar.matcher(line);
        if(matcher.matches()) {
            name = matcher.group(1).trim();
            log.debug("Matched grammar name: {}", name);
            return true;
        }
        return false;
    }

    private boolean processLexerLine(String line) {
        Matcher matcher = lexerLinePattern.matcher(line);
        if(matcher.matches()) {
            String[] splitLine = line.split(":");
            String terminalName = splitLine[0].trim();
            String terminalRegex = removeSemicolon(splitLine[1]);

            if(terminals.containsKey(terminalName)) {
                log.warn("There is already terminal `{}` defined. It's definition will be overwritten", terminalName);
            }
            terminals.put(terminalName, Terminal.of(terminalName, terminalRegex));

            return true;
        } else {
            log.trace("Line '{}' does not match lexer rule.", line);
        }

        return false;
    }

    private boolean processParserLine(String line) {
        Matcher matcher = parserLinePattern.matcher(line);
        if(matcher.matches()) {
            String[] splitLine = line.split(":");
            String leftHandSideNonTerminalName = splitLine[0].trim();
            NonTerminal lhs = NonTerminal.of(leftHandSideNonTerminalName);

            String rightHandSideProductions = removeSemicolon(splitLine[1]);
            String[] rightHandSideProduction = rightHandSideProductions.split("\\|");

            for(String rightHandSideSymbols : rightHandSideProduction) {
                String[] symbolNames = rightHandSideSymbols.trim().split("\\s");

                List<Symbol> rhs = convertSymbolNamesToSymbols(symbolNames);

                productions.add(Production.of(lhs, rhs));
            }

            return true;
        } else {
            log.trace("Line '{}' does not match parser rule.", line);
        }

        return false;
    }

    /**
     * Removes ending semicolon from the line
     * @param line
     * @return
     */
    private String removeSemicolon(String line) {
        if(line.endsWith(";")) {
            line = line.substring(0, line.length() - 1).trim();
        }
        return line;
    }

    /**
     * Converts right-hand side symbol names extracted from the grammar file to Symbol
     * instances list
     * @param symbolNames
     * @return
     */
    private List<Symbol> convertSymbolNamesToSymbols(String[] symbolNames) {
        return Arrays.stream(symbolNames).map(symbol -> {
            if(terminalPattern.matcher(symbol).matches()) {
                if (terminals.containsKey(symbol)) {
                    return terminals.get(symbol);
                } else {
                    throw new IllegalStateException("Undefined terminal " + symbol);
                }
            } else if(nonTerminalPattern.matcher(symbol).matches()) {
                return NonTerminal.of(symbol);
            } else { // in theory should not happen because of lexer/parser rules
                throw new IllegalStateException("Illegal symbol name");
            }
        }).collect(Collectors.toList());
    }

    Set<Terminal> getTerminals() {
        return new HashSet<>(terminals.values());
    }
}
