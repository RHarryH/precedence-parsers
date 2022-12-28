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

package com.avispa.parser.misc.cli;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.misc.tree.TreePrinter;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.output.Derivation;
import com.avispa.parser.precedence.output.ParseTree;
import com.avispa.parser.precedence.parser.ParserCreationException;
import com.avispa.parser.precedence.parser.ParserFactory;
import com.avispa.parser.precedence.parser.PrecedenceParser;
import com.avispa.parser.precedence.parser.SyntaxException;
import com.avispa.parser.shuntingyard.ShuntingYard;
import com.avispa.parser.shuntingyard.output.Evaluator;
import com.avispa.parser.shuntingyard.output.ExpressionTree;
import com.avispa.parser.shuntingyard.output.ReversePolishNotationText;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

import static com.avispa.parser.misc.cli.OutputMode.DERIVATION;
import static com.avispa.parser.misc.cli.OutputMode.EVALUATED;
import static com.avispa.parser.misc.cli.OutputMode.EXPRESSION_TREE;
import static com.avispa.parser.misc.cli.OutputMode.PARSE_TREE;
import static com.avispa.parser.misc.cli.OutputMode.PRODUCTION_LIST;
import static com.avispa.parser.misc.cli.OutputMode.REVERSE_POLISH_NOTATION;
import static com.avispa.parser.misc.cli.OutputMode.TOKEN_LIST;
import static com.avispa.parser.misc.cli.ToolOption.GRAMMAR;
import static com.avispa.parser.misc.cli.ToolOption.HELP;
import static com.avispa.parser.misc.cli.ToolOption.INPUT;
import static com.avispa.parser.misc.cli.ToolOption.OUTPUT;
import static com.avispa.parser.misc.cli.ToolOption.START;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class CliService {
    public String process(String[] args) throws MissingArgumentException {
        CommandLineParser cmdParser = new DefaultParser();
        Options options = getOptions();
        CommandLine commandLine = getCommandLine(args, options, cmdParser);
        if(null == commandLine) {
            return "";
        }

        if(commandLine.hasOption(HELP.getName())) {
            new HelpFormatter().printHelp("precedence-parsers [OPTIONS]", options);
            return "";
        }

        String input;
        if(commandLine.hasOption(INPUT.getName())) {
            input = commandLine.getOptionValue(INPUT.getName());
        } else {
            throw new MissingArgumentException("Input is mandatory");
        }

        try {
            if (commandLine.hasOption(GRAMMAR.getName())) {
                return getPrecedenceParserOutput(commandLine, input);
            } else {
                return getShuntingYardOutput(commandLine, input);
            }
        } catch(ParserCreationException | SyntaxException | LexerException e) {
            log.error("Parsing failed: {}", e.getMessage());
        }

        return "";
    }

    private CommandLine getCommandLine(String[] args, Options options, CommandLineParser cmdParser) {
        try {
            return cmdParser.parse(options, args);
        } catch (ParseException e) {
            log.error("Can't parse the options: {}", e.getMessage());
        }

        return null;
    }

    private Options getOptions() {
        Options options = new Options();

        for(ToolOption option : ToolOption.values()) {
            options.addOption(option.getName(), option.getLongName(), option.isHasArguments(), option.getDescription());
        }

        return options;
    }

    private String getPrecedenceParserOutput(CommandLine commandLine, String input) throws SyntaxException, LexerException, ParserCreationException {
        try {
            Grammar grammar = loadGrammar(commandLine);
            PrecedenceParser<Production> parser = ParserFactory.newSimplePrecedenceParser(grammar);

            String output = "";
            if (commandLine.hasOption(OUTPUT.getName())) {
                String value = commandLine.getOptionValue(OUTPUT.getName());

                if (PARSE_TREE.getName().equals(value)) {
                    output = TreePrinter.print(new ParseTree(parser).parse(input));
                } else if (DERIVATION.getName().equals(value)) {
                    output = new Derivation(parser).parse(input).toString();
                } else if (PRODUCTION_LIST.getName().equals(value)) {
                    output = parser.parse(input).toString();
                } else {
                    log.error("Unknown or unsupported output type for precedence grammar: {}", value);
                }
            } else { // by default list of productions
                output = parser.parse(input).toString();
            }
            return output;
        } catch(IncorrectGrammarException | IOException e) {
            log.error("Can't read grammar: {}", e.getMessage());
        }

        return "";
    }

    private Grammar loadGrammar(CommandLine commandLine) throws IOException, IncorrectGrammarException {
        String grammarFilePath = commandLine.getOptionValue(GRAMMAR.getName());
        if(!commandLine.hasOption(START.getName())) {
            throw new IncorrectGrammarException("Start symbol is not defined");
        }

        GrammarFile grammarFile = new GrammarFile(grammarFilePath);
        NonTerminal start = NonTerminal.of(commandLine.getOptionValue(START.getName()));
        Grammar grammar = ContextFreeGrammar.fromWithBoundaryMarker(grammarFile, start);

        log.info("Loaded grammar:");
        log.info("{}", grammar);

        return grammar;
    }

    private String getShuntingYardOutput(CommandLine commandLine, String input) throws SyntaxException, LexerException {
        String output = "";
        if (commandLine.hasOption(OUTPUT.getName())) {
            String value = commandLine.getOptionValue(OUTPUT.getName());

            if (EVALUATED.getName().equals(value)) {
                output = new Evaluator().parse(input).toPlainString();
            } else if (EXPRESSION_TREE.getName().equals(value)) {
                output = TreePrinter.print(new ExpressionTree().parse(input));
            } else if (REVERSE_POLISH_NOTATION.getName().equals(value)) {
                output = new ReversePolishNotationText().parse(input);
            } else if (TOKEN_LIST.getName().equals(value)) {
                output = new ShuntingYard().parse(input).toString();
            } else {
                log.error("Unknown or unsupported output type for shunting-yard algorithm: {}.", value);
            }
        } else { // by default list of tokens
            output = new ShuntingYard().parse(input).toString();
        }
        return output;
    }
}
