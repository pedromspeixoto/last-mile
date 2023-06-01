package com.lastmile.utils.logs;

import javax.servlet.http.HttpServletRequest;

import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class CustomLogging {

    @Value("${lastmile.log.requests}")
    private boolean shouldLog;

    private static final Logger logger = LoggerFactory.getLogger(CustomLogging.class);

    public CustomLogging() {
    }

    public void info(String message) {
        if (this.shouldLog) logger.info(message);
    }

    public void info(String message, String requestId, String correlationId) {
        if (this.shouldLog) logger.info(this.buildLogHeader(requestId, correlationId) + message);
    }

    public void info(String message, ServiceContext serviceContext) {
        if (this.shouldLog) logger.info(this.buildLogHeader(serviceContext) + message);
    }

    public void info(String message, HttpServletRequest request) {
        if (this.shouldLog) logger.info(this.buildLogHeader(request) + message);
    }

    public void error(String message) {
        if (this.shouldLog) logger.error(message);
    }

    public void error(String message, ServiceContext serviceContext) {
        if (this.shouldLog) logger.error(this.buildLogHeader(serviceContext) + message);
    }

    public void error(String message, HttpServletRequest request) {
        if (this.shouldLog) logger.error(this.buildLogHeader(request) + message);
    }

    private String buildLogHeader(String requestId, String correlationId) {
        return "Logging: " + Constants.JSON_VALUE_REQUEST_ID + ": " + requestId + "; "
                           + Constants.JSON_VALUE_CORRELATION_ID + ": " + correlationId + "; ";
    }    

    private String buildLogHeader(ServiceContext serviceContext) {
        return "Logging: " + Constants.JSON_VALUE_REQUEST_ID + ": " + serviceContext.getRequestId() + "; "
                           + Constants.JSON_VALUE_CORRELATION_ID + ": " + serviceContext.getCorrelationId() + "; ";
    }

    private String buildLogHeader(HttpServletRequest request) {
        return "Logging: " + Constants.JSON_VALUE_REQUEST_ID + ": " + request.getHeader(Constants.JSON_VALUE_REQUEST_ID) + "; "
                           + Constants.JSON_VALUE_CORRELATION_ID + ": " + request.getHeader(Constants.JSON_VALUE_CORRELATION_ID) + "; ";
    }

}