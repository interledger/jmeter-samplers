package org.interledger.jmeter.samplers;

import org.apache.log.Logger;
import org.apache.log.Priority;
import org.slf4j.LoggerFactory;

/**
 * Hacky wrapper around slf4j to match logging interface on WebSocket sampler APIs
 * which still use deprecated apache logger
 */
public class Slf4jLogger extends Logger {

    private final org.slf4j.Logger log;

    public Slf4jLogger(org.slf4j.Logger log) {
        this.log = log;
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(String message, Throwable throwable) {
        log.debug(message, throwable);
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String message, Throwable throwable) {
        log.info(message, throwable);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log.warn(message, throwable);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public boolean isFatalErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    @Override
    public void fatalError(String message) {
        log.error(message);
    }

    @Override
    public boolean isPriorityEnabled(Priority priority) {
        if(priority.getValue() == Priority.INFO.getValue()) {
            return log.isInfoEnabled();
        }
        if(priority.getValue() == Priority.DEBUG.getValue()) {
            return log.isDebugEnabled();
        }
        if(priority.getValue() == Priority.WARN.getValue()) {
            return log.isWarnEnabled();
        }
        if(priority.getValue() == Priority.ERROR.getValue()) {
            return log.isErrorEnabled();
        }
        if(priority.getValue() == Priority.FATAL_ERROR.getValue()) {
            return log.isErrorEnabled();
        }
        return false;
    }

    @Override
    public void log(Priority priority, String message, Throwable throwable) {
        if(priority.getValue() == Priority.INFO.getValue()) {
            log.info(message, throwable);
            return;
        }
        if(priority.getValue() == Priority.DEBUG.getValue()) {
            log.debug(message, throwable);
            return;
        }
        if(priority.getValue() == Priority.WARN.getValue()) {
            log.warn(message, throwable);
            return;
        }
        if(priority.getValue() == Priority.ERROR.getValue()) {
            log.error(message, throwable);
            return;
        }
        if(priority.getValue() == Priority.FATAL_ERROR.getValue()) {
            log.error(message, throwable);
            return;
        }
    }

    @Override
    public void log(Priority priority, String message) {
        if(priority.getValue() == Priority.INFO.getValue()) {
            log.info(message);
            return;
        }
        if(priority.getValue() == Priority.DEBUG.getValue()) {
            log.debug(message);
            return;
        }
        if(priority.getValue() == Priority.WARN.getValue()) {
            log.warn(message);
            return;
        }
        if(priority.getValue() == Priority.ERROR.getValue()) {
            log.error(message);
            return;
        }
        if(priority.getValue() == Priority.FATAL_ERROR.getValue()) {
            log.error(message);
            return;
        }
    }

    @Override
    public Logger getChildLogger(String subCategory) {
        return new Slf4jLogger(LoggerFactory.getLogger(log.getName() + ":" + subCategory));
    }
}
