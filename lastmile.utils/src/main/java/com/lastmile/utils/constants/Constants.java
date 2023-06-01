package com.lastmile.utils.constants;

public class Constants {
    // string constants
    public static final String YES_VALUE = "Y";
    public static final String AUTHORIZATION = "Authorization";

    // message constants
    public static final String NEW_ORDER_NOTIFICATION_MESSAGE = "New order assigned. Please accept/reject this request using the GoMile App.";

    // default values
    public static final Integer DEFAULT_VALUE_LIMIT = 10;
    public static final Integer DEFAULT_VALUE_OFFSET = 0;

    // roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_DRIVER = "ROLE_DRIVER";
    public static final String ROLE_USER = "ROLE_USER";

    // entities
    public static final String ENTITY_FISCALENTITY = "FISCALENTITY";

    // batches
    public static final String ASSIGNED_ORDERS_BATCH_NAME = "ASSIGNED_ORDERS_BATCH";
    public static final String SCHEDULED_ORDERS_BATCH_NAME = "SCHEDULED_ORDERS_BATCH";
    public static final String OUTBOUND_PAYMENTS_BATCH_NAME = "OUTBOUND_PAYMENTS_BATCH";

    // request origins
    public static final String REQUEST_ORIGIN_INTERNAL = "internal";
    public static final String REQUEST_ORIGIN_BATCH = "batch";
    public static final String REQUEST_ORIGIN_RABBITMQ = "rabbitmq";
    public final static String REQUEST_ORIGIN_QUARTZ = "quartz";
    public static final String REQUEST_ORIGIN_ENTITY_ACCOUNT = "account";
    public static final String REQUEST_ORIGIN_ENTITY_PAYMENT = "payment";
    public static final String REQUEST_ORIGIN_ENTITY_OUTBOUND_PAYMENT = "outbound_payment";
    public static final String REQUEST_ORIGIN_ENTITY_ORDER = "order";
    public static final String REQUEST_ORIGIN_ENTITY_CUSTOMER = "customer";
    public static final String REQUEST_ORIGIN_ENTITY_DRIVER = "driver";

    // service context headers
    public static final String JSON_VALUE_ACCEPT = "Accept";
    public static final String JSON_VALUE_REQUEST_ID = "request_id";
    public static final String JSON_VALUE_CORRELATION_ID = "correlation_id";
    public static final String JSON_VALUE_USER_ID = "user_id";
    public static final String JSON_VALUE_PERMISSIONS = "role";
    public static final String JSON_VALUE_TIMESTAMP = "timestamp";
    public static final String JSON_VALUE_REQUEST_ORIGIN = "request_origin";
    public static final String JSON_VALUE_REQUEST_ENTITY = "request_entity";
    public static final String JSON_VALUE_REQUEST_ENTITY_ID = "request_entity_id";
    public static final String JSON_VALUE_API_KEY = "X-Api-Key";
}