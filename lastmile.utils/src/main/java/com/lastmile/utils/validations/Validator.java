package com.lastmile.utils.validations;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.constants.Constants;

import org.apache.commons.validator.routines.EmailValidator;

public class Validator {

    public static boolean isValidEmail(String email) {

        // create the EmailValidator instance
        EmailValidator validator = EmailValidator.getInstance();

        // check for valid email addresses using isValid method
        return validator.isValid(email);

    }

    public static boolean isValidPhoneNumber(String phoneNumber) {

        String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
        Pattern pattern = Pattern.compile(allCountryRegex);

        return pattern.matcher(phoneNumber).matches();

    }

    public static boolean isAuthenticated(ServiceContext serviceContext) {

        if (null == serviceContext.getUserId()) {
            return false;
        }

        return true;

    }

    public static boolean isAdmin(ServiceContext serviceContext) {

        String permissionsHeader = serviceContext.getPermissions();

        if (null == permissionsHeader) {
            return false;
        }

        if (!permissionsHeader.contains(Constants.ROLE_ADMIN)) {
            return false;
        }

        return true;

    }

    public static boolean isInternalCommunication(ServiceContext serviceContext) {

        String originHeader = serviceContext.getRequestOrigin();

        if (null == originHeader) {
            return false;
        }

        if (!originHeader.contains(Constants.REQUEST_ORIGIN_INTERNAL)) {
            return false;
        }

        return true;

    }

    public static boolean isOwner(ServiceContext serviceContext, String ownerIdentification) {

        String headerUserId = serviceContext.getUserId();

        if (null == ownerIdentification) {
            return false;
        }

        if (null == headerUserId) {
            return false;
        }

        if (!headerUserId.equals(ownerIdentification)) {
            return false;
        }

        return true;
    }

    public static boolean isSameEntity(ServiceContext serviceContext, String entityId, String entityType) {

        String headerEntity = serviceContext.getRequestEntity();
        String headerEntityId = serviceContext.getRequestEntityId();

        if (null == entityId || null == entityType) {
            return false;
        }

        if (null == headerEntity || null == headerEntityId) {
            return false;
        }

        if (!headerEntity.equalsIgnoreCase(entityType)) {
            return false;
        }    

        if (!headerEntityId.equals(entityId)) {
            return false;
        }

        return true;
    }

    public static boolean isSameUser(ServiceContext serviceContext, String requestUserId) {

        String headerUserId = serviceContext.getUserId();

        if (null == requestUserId) {
            return false;
        }

        if (null == headerUserId) {
            return false;
        }

        if (!headerUserId.equals(requestUserId)) {
            return false;
        }

        return true;
    }

    public static boolean isEntityAllowed(ServiceContext serviceContext, Optional<String> entityId, Optional<String> entityType) {

        if (isSameUser(serviceContext, entityId.orElse(null))) {
            return true;
        }

        if (isInternalCommunication(serviceContext) && isSameEntity(serviceContext, entityId.orElse(null), entityType.orElse(null))) {
            return true;
        }

        return false;
    }

    public static boolean isDateInTheFuture(Date referenceDate, Long offset) {
        // create a date after the currente date with the specified offset (in minutes)
        long offsetMilliseconds = System.currentTimeMillis() + 1000*60*offset;
        // create date object
        Date offsetDate = new Date(offsetMilliseconds);
        // compare both dates
        if (referenceDate.after(offsetDate)) {
            return true;
        }
        return false;
    }

    public static boolean isDateInTheFuture(Date referenceDate) {
        Date current = new Date();
        // compare both dates
        if (referenceDate.after(current)) {
            return true;
        }
        return false;
    }

    public static boolean isOriginOutboundPayment(ServiceContext serviceContext) {

        String requestEntity = serviceContext.getRequestEntity();

        if (null == requestEntity) {
            return false;
        }

        if (!requestEntity.contains(Constants.REQUEST_ORIGIN_ENTITY_OUTBOUND_PAYMENT)) {
            return false;
        }

        return true;

    }

    public static boolean isSameDriver(ServiceContext serviceContext, String assignedDriver) {

        String headerEntity = serviceContext.getRequestEntity();
        String headerEntityId = serviceContext.getRequestEntityId();

        if (null == assignedDriver) {
            return false;
        }

        if (null == headerEntity) {
            return false;
        }

        if (!headerEntity.equals(Constants.REQUEST_ORIGIN_ENTITY_DRIVER)) {
            return false;
        }

        if (null == headerEntityId) {
            return false;
        }

        if (!headerEntityId.equals(assignedDriver)) {
            return false;
        }

        return true;
    }

    public static boolean isSameCustomer(ServiceContext serviceContext, String requestCustomerId) {

        String headerEntity = serviceContext.getRequestEntity();
        String headerEntityId = serviceContext.getRequestEntityId();

        if (null == requestCustomerId) {
            return false;
        }

        if (null == headerEntity) {
            return false;
        }

        if (!headerEntity.equals(Constants.REQUEST_ORIGIN_ENTITY_CUSTOMER)) {
            return false;
        }

        if (null == headerEntityId) {
            return false;
        }

        if (!headerEntityId.equals(requestCustomerId)) {
            return false;
        }

        return true;
    }

    public static boolean isSameEntity(ServiceContext serviceContext, Optional<String> requesterId, Optional<String> assignedDriver, Optional<String> ownerIdentification) {

        if (isSameUser(serviceContext, requesterId.orElse(null))) {
            return true;
        }

        if (isOwner(serviceContext, ownerIdentification.orElse(null))) {
            return true;
        }

        if (isInternalCommunication(serviceContext) && isSameCustomer(serviceContext, requesterId.orElse(null))) {
            return true;
        }

        if (isInternalCommunication(serviceContext) && isSameDriver(serviceContext, assignedDriver.orElse(null))) {
            return true;
        }

        return false;
    }

    public static boolean isBatch(ServiceContext serviceContext) {

        String originHeader = serviceContext.getRequestOrigin();

        if (null == originHeader) {
            return false;
        }

        if (!originHeader.contains(Constants.REQUEST_ORIGIN_BATCH)) {
            return false;
        }

        return true;

    }

    public static boolean isFiscalEntity(ServiceContext serviceContext) {

        String entityType = serviceContext.getRequestEntity();

        if (null == entityType) {
            return false;
        }

        if (!entityType.toUpperCase().contains(Constants.ENTITY_FISCALENTITY)) {
            return false;
        }

        return true;

    }

}