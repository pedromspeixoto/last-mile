package com.lastmile.gatewayservice.filter;

import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.logs.CustomLogging;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;

@Component
public class OutputLoggingFilter extends ZuulFilter {

    @Autowired
    CustomLogging logger;

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        String requestId = "";
        String correlationId = "";

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
        
        List<Pair<String, String>> zuulResponseHeaders = RequestContext.getCurrentContext().getZuulResponseHeaders();
        if (zuulResponseHeaders != null) {
            for (Pair<String, String> header : zuulResponseHeaders) {
                if (header.first().equals(Constants.JSON_VALUE_REQUEST_ID)) requestId = header.second();
                if (header.first().equals(Constants.JSON_VALUE_CORRELATION_ID)) correlationId = header.second();
            }
        }

        long startTime = (Long) request.getAttribute("startTime");

        logger.info("response HTTP status: " + response.getStatus() 
                    + "; Elapsed Time: " + (Instant.now().toEpochMilli() - startTime) + "ms",
                    requestId,
                    correlationId);

        return null;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}