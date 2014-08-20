package org.motechproject.whp.mtraining.service.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class ErrorLogService {
    StringWriter logWriter = null;

    public ErrorLogService() {
        logWriter = new StringWriter();
        WriterAppender appender = new WriterAppender(new PatternLayout("%d{ISO8601} %-5p [%c] - %m%n"), logWriter);
        appender.setName("errorLogService");
        appender.setThreshold(Level.ERROR);
        Logger.getRootLogger().addAppender(appender);
    }

    public String getErrorLogs() {
        return logWriter.getBuffer().toString();
    }
}
