package com.lastmile.utils.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class ErrorResponse extends ApiResponse {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponse.class);

    // Json Properties
    private static final String JSON_VALUE_ID = "id";
    private static final String JSON_VALUE_TIMESTAMP = "timestamp";
    private static final String JSON_VALUE_CODE = "code";
    private static final String JSON_VALUE_MESSAGE = "message";
    private static final String JSON_VALUE_ERRORS = "errors";

    // HttpServletResponse
    private static final String JSON_CONTENT_TYPE = "application/json";

    @JsonProperty(value = JSON_VALUE_ERRORS)

    private List<String> errors;

    public ErrorResponse(int code, String message) {

        super(code, message);
    }

    public ErrorResponse(int code, String message, String error) {

        super(code, message);
        this.errors = new ArrayList<String>();
        this.errors.add(error);
    }

    public ErrorResponse(int code, String message, List<String> errors) {

        super(code, message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public HttpServletResponse buildHttpServletResponse(HttpServletResponse response) {

        response.setContentType(JSON_CONTENT_TYPE);
        response.setStatus(this.getCode());

        ObjectMapper mapper = new ObjectMapper();
        try {

            response.getWriter().write(mapper.writeValueAsString(this));

        } catch (IOException ex) {

            logger.error(ex.getMessage(), ex);
        }

        return response;
    }

    public Map<String, Object> toMap() {

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(JSON_VALUE_ID, this.getId());
        map.put(JSON_VALUE_TIMESTAMP, this.getTimestamp());
        map.put(JSON_VALUE_CODE, this.getCode());
        map.put(JSON_VALUE_MESSAGE, this.getMessage());
        map.put(JSON_VALUE_ERRORS, this.errors);

        return map;
    }

    @Override
    public String toString() {
        return "ErrorResponse [errors=" + errors + "]";
    }

}