package com.avispa.parser;

import com.avispa.parser.misc.cli.CliService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.MissingArgumentException;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class PrecedenceParsers {
    public static void main(String[] args) throws MissingArgumentException {
        log.info(new CliService().process(args));
    }
}
