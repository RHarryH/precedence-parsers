package com.avispa.parser.precedence.grammar;

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

    private final String fileName;

    private final Map<String, Terminal> terminals = new HashMap<>();
    private final List<Production> productions = new ArrayList<>();
    private String name;

    public GrammarFile(String fileName) {
        this.fileName = fileName;
    }

    public ContextFreeGrammar read() throws IncorrectGrammarException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(this::parseLine);
        } catch (IOException e) {
            log.error("Can't read input grammar file: {}", fileName, e);
        }

        return new ContextFreeGrammar(name, new HashSet<>(terminals.values()), productions);
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
            if(log.isDebugEnabled()) {
                log.debug("Matched grammar name: {}", name);
            }
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
        } else if(log.isDebugEnabled()) {
            log.warn("Line '{}' does not match lexer rule.", line);
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

            for(String rightHandSideTokens : rightHandSideProduction) {
                String[] tokenNames = rightHandSideTokens.trim().split("\\s");

                List<GenericToken> rhs = convertTokenNamesToTokens(tokenNames);

                productions.add(Production.of(lhs, rhs));
            }

            return true;
        } else if(log.isDebugEnabled()) {
            log.warn("Line '{}' does not match parser rule.", line);
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
     * Converts right-hand side token names extracted from the grammar file to GenericToken
     * instances list
     * @param tokenNames
     * @return
     */
    private List<GenericToken> convertTokenNamesToTokens(String[] tokenNames) {
        return Arrays.stream(tokenNames).map(token -> {
            if(terminalPattern.matcher(token).matches()) {
                if (terminals.containsKey(token)) {
                    return terminals.get(token);
                } else {
                    throw new IllegalStateException("Undefined terminal " + token);
                }
            } else if(nonTerminalPattern.matcher(token).matches()) {
                return NonTerminal.of(token);
            } else { // in theory should not happen because of lexer/parser rules
                throw new IllegalStateException("Illegal token name");
            }
        }).collect(Collectors.toList());
    }
}
