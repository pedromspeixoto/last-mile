package com.lastmile.gatewayservice.filter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lastmile.gatewayservice.client.OAuthTokenClient;
import com.lastmile.gatewayservice.dto.AuthServerUserDetailsDto;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.logs.CustomLogging;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class CorrelationFilter extends ZuulFilter {

    CustomLogging logger;
    OAuthTokenClient oauthTokenClient;

    @Autowired
    public CorrelationFilter(OAuthTokenClient oauthTokenClient,
                             CustomLogging logger) {
        this.oauthTokenClient = oauthTokenClient;
        this.logger = logger;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        AuthServerUserDetailsDto auth = new AuthServerUserDetailsDto();

        String requestId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();

        ctx.addZuulRequestHeader(Constants.JSON_VALUE_REQUEST_ID, requestId);
        ctx.addZuulRequestHeader(Constants.JSON_VALUE_CORRELATION_ID, correlationId);
        ctx.addZuulRequestHeader(Constants.JSON_VALUE_TIMESTAMP, String.valueOf(LocalDateTime.now()));

        ctx.addZuulResponseHeader(Constants.JSON_VALUE_REQUEST_ID, requestId);
        ctx.addZuulResponseHeader(Constants.JSON_VALUE_CORRELATION_ID, correlationId);

        request.setAttribute("startTime", Instant.now().toEpochMilli());

        // reject security headers forced through the gateway
        if (null != request.getHeader(Constants.JSON_VALUE_USER_ID) 
                || null != request.getHeader(Constants.JSON_VALUE_PERMISSIONS) 
                || ( null != request.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN) && request.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN).equals(Constants.REQUEST_ORIGIN_INTERNAL)
                || ( null != request.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN) && request.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN).equals(Constants.REQUEST_ORIGIN_BATCH)) )) {
            ctx.setResponse(response);
            ctx.getResponse().setStatus(HttpStatus.SC_UNAUTHORIZED);
            return null;
        }

        // check if authorization header exists
        String authorizationHeader = request.getHeader(Constants.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            try {
                auth = oauthTokenClient.getUserDetails(authorizationHeader);
            } catch (Exception e) {
                ctx.setResponse(response);
                ctx.getResponse().setStatus(HttpStatus.SC_UNAUTHORIZED);
                return null;
            }
            // if token is invalid or expired, the user is unauthorized
            if (auth == null) {
                ctx.setResponse(response);
                ctx.getResponse().setStatus(HttpStatus.SC_UNAUTHORIZED);
                return null;
            }
            // add user_id and role to headers
            ctx.addZuulRequestHeader(Constants.JSON_VALUE_USER_ID, auth.getUserIdentification());
            ctx.addZuulRequestHeader(Constants.JSON_VALUE_PERMISSIONS, auth.getRole());
        }

        logger.info("incoming HTTP request: " + request.getMethod() + " " + request.getRequestURL().toString()
                    + (null != request.getQueryString() ? "?" + request.getQueryString() : "")
                    + (null != auth.getUsername() ? "; user_id: " + auth.getUserIdentification() : "")
                    + (null != auth.getRole() ? "; role: " + auth.getRole() : "")
                    + "; remote_address:" + request.getRemoteAddr(),
                    requestId,
                    correlationId);

        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

}