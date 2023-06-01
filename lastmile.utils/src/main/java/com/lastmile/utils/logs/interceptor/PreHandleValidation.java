package com.lastmile.utils.logs.interceptor;

import javax.servlet.http.HttpServletRequest;

import com.lastmile.utils.constants.Constants;

public abstract class PreHandleValidation {

    public static boolean hasRequestId(HttpServletRequest request) {

        if (null == request.getHeader(Constants.JSON_VALUE_REQUEST_ID)) {
            return false;
        }

        return true;
    }

    public static boolean hasCorrelationId(HttpServletRequest request) {

        if (null == request.getHeader(Constants.JSON_VALUE_CORRELATION_ID)) {
            return false;
        }

        return true;
    }

    public static boolean isAuthenticated(HttpServletRequest request) {

        if (null == request.getHeader(Constants.JSON_VALUE_USER_ID)) {
            return false;
        }

        return true;
    }

    public static boolean hasAdminAuthority(HttpServletRequest request) {

        String permissionsHeader = request.getHeader(Constants.JSON_VALUE_PERMISSIONS);

        if (null == permissionsHeader) {
            return false;
        }

        if (!permissionsHeader.contains(Constants.ROLE_ADMIN)) {
            return false;
        }
        return true;
    }

    public static boolean checkSameUser(HttpServletRequest request, String userIdentification) {

        String headerUserId = request.getHeader(Constants.JSON_VALUE_USER_ID);

        if (null == headerUserId) {
            return false;
        }

        if (!headerUserId.equals(userIdentification)) {
            return false;
        }
        return true;
    }

    public static boolean isFeignRequest(HttpServletRequest request) {

        String requestOrigin = request.getHeader(Constants.JSON_VALUE_REQUEST_ORIGIN);

        if (null == requestOrigin) {
            return false;
        }

        if (!requestOrigin.equals(Constants.REQUEST_ORIGIN_INTERNAL)) {
            return false;
        }
        return true;
    }

}