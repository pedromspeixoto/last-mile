package com.lastmile.utils.logs.interceptor;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lastmile.utils.constants.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        logger.info("Incoming: " + Constants.JSON_VALUE_REQUEST_ID + ": " + request.getHeader(Constants.JSON_VALUE_REQUEST_ID) + "; "
            + Constants.JSON_VALUE_CORRELATION_ID + ": " + request.getHeader(Constants.JSON_VALUE_CORRELATION_ID)
            + (null != request.getHeader(Constants.JSON_VALUE_USER_ID) ? "; user_id: " + request.getHeader(Constants.JSON_VALUE_USER_ID) : "")
            + (null != request.getHeader(Constants.JSON_VALUE_PERMISSIONS) ? "; role: " + request.getHeader(Constants.JSON_VALUE_PERMISSIONS) : "")
            + "; HTTP "+ request.getMethod() + " " + request.getRequestURL().toString()
            + (null != request.getQueryString() ? "?" + request.getQueryString() : "")
            + "; remote_address:" + request.getRemoteAddr());

        request.setAttribute("startTime", Instant.now().toEpochMilli());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) {

        long startTime = (Long) request.getAttribute("startTime");

        logger.info("Response: " + Constants.JSON_VALUE_REQUEST_ID + ": " + request.getHeader(Constants.JSON_VALUE_REQUEST_ID) + "; "
                                 + Constants.JSON_VALUE_CORRELATION_ID + ": " + request.getHeader(Constants.JSON_VALUE_CORRELATION_ID) + "; HTTP Status: "
                                 + response.getStatus() + "; Elapsed Time: " + (Instant.now().toEpochMilli() - startTime) + "ms");

    }
}