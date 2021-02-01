package com.github.bluemustard2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Arrays;

public class Log {
    private static final Logger LOGGER;

    static {
        Configurator.setRootLevel(Level.INFO);
        LOGGER = LogManager.getLogger("BotCast");
    }

    public static void info(String message, Object...args) {
        log(Level.INFO, message, args);
    }

    public static void warn(String message, Object...args) {
        log(Level.WARN, message, args);
    }

    public static void error(String message, Object... args) {
        log(Level.ERROR, message, args);
    }

    public static void printStackTrace(StackTraceElement[] elements) {
        Arrays.stream(elements).forEach(element -> warn(element.toString()));
    }

    private static void log(Level level, String message, Object... args) {
        LOGGER.log(level, String.format(message, args));
    }
}
