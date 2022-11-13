package com.avispa.parser.misc.cli;

import com.avispa.parser.misc.tree.TreePrinter;
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

import static com.avispa.parser.misc.cli.OutputMode.EVALUATED;
import static com.avispa.parser.misc.cli.OutputMode.EXPRESSION_TREE;
import static com.avispa.parser.misc.cli.OutputMode.REVERSE_POLISH_NOTATION;
import static com.avispa.parser.misc.cli.ToolOption.HELP;
import static com.avispa.parser.misc.cli.ToolOption.INPUT;
import static com.avispa.parser.misc.cli.ToolOption.OUTPUT;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class CliService {
    public String process(String[] args) throws MissingArgumentException {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();
        CommandLine commandLine = getCommandLine(args, options, parser);

        if(commandLine.hasOption(HELP.getName())) {
            new HelpFormatter().printHelp("precedence-parsers [OPTIONS]", options);
            return "";
        }

        String expression;
        if(commandLine.hasOption(INPUT.getName())) {
            expression = commandLine.getOptionValue(INPUT.getName());
        } else {
            throw new MissingArgumentException("Input is mandatory");
        }

        if(commandLine.hasOption(OUTPUT.getName())) {
            String value = commandLine.getOptionValue(OUTPUT.getName());

            if(EVALUATED.getName().equals(value)) {
                return new Evaluator().parse(expression).toPlainString();
            } else if(EXPRESSION_TREE.getName().equals(value)) {
                return TreePrinter.print(new ExpressionTree().parse(expression));
            } else if(REVERSE_POLISH_NOTATION.getName().equals(value)) {
                return new ReversePolishNotationText().parse(expression);
            } else {
                return new ShuntingYard().parse(expression).toString();
            }
        } else { // by default list of tokens
            return new ShuntingYard().parse(expression).toString();
        }
    }

    private CommandLine getCommandLine(String[] args, Options options, CommandLineParser parser) throws MissingArgumentException {
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            log.error("Can't parse the input options", e);
            throw new MissingArgumentException("Can't parse the input options");
        }
    }

    private Options getOptions() {
        Options options = new Options();

        for(ToolOption option : ToolOption.values()) {
            options.addOption(option.getName(), option.getLongName(), option.isHasArguments(), option.getDescription());
        }

        return options;
    }
}
