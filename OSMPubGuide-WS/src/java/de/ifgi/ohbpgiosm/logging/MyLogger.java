/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifgi.ohbpgiosm.logging;

import org.apache.log4j.*;

/**
 *
 * @author christopher
 */
public class MyLogger {

    private static MyLogger instance = null;
    private Logger logger;

    private MyLogger() {
        logger = Logger.getRootLogger();
        initLogger(logger);
    }

    public static MyLogger getInstance() {
        if (instance == null) {
            instance = new MyLogger();
        }
        return instance;
    }

    public Logger getLogger() {
        return this.logger;
    }

    private void initLogger(Logger logger) {
        try {

            PatternLayout layout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");

            ConsoleAppender consoleAppender = new ConsoleAppender(layout);
            logger.addAppender(consoleAppender);

            DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(layout, "logs/logs.log", "'.'yyyy-MM-dd_HH-mm");
            logger.addAppender(fileAppender);

            logger.setLevel(Level.ALL);

        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
