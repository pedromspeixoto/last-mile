CREATE TABLE IF NOT EXISTS orders_history (
    action varchar(20),
    id serial,
    order_identification varchar(255),
    short_order_identification varchar(255),
    order_external_identification varchar(255),
    requester_identification varchar(255),
    requester_entity_name varchar(255),
    requester_entity_type varchar(255),
    requester_name varchar(255),
    requester_phone_number varchar(255),
    requester_email varchar(255),
    requester_country varchar(255),
    requester_city varchar(255),
    requester_zip_code varchar(255),
    pickup_latitude float,
    pickup_longitude float,
    pickup_address varchar(255),
    destination_country varchar(255),
    destination_city varchar(255),
    destination_zip_code varchar(255),
    destination_latitude float,
    destination_longitude float,
    destination_address varchar(255),
    pickup_photo varchar(255),
    delivery_photo varchar(255),
    order_ext_voice_session varchar(255),
    order_ext_message_session varchar(255),
    status varchar(255),
    priority varchar(255),
    scheduled_date timestamp,
    assigned_driver varchar(255),
    delivery_fee_value numeric,
    driver_fee_value numeric,
    order_value numeric,
    order_type varchar(255),
    order_rating int,
    estimated_distance int,
    packaging_eta int,
    pickup_eta int,
    delivery_eta int,
    effective_pickup_time int,
    effective_delivery_time int,
    owner_identification varchar(255),
    is_order_tracking_active boolean,
    payment_status varchar(255),
    driver_payment_status varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

-- create trigger function
CREATE OR REPLACE FUNCTION orders_history_trigger_function() RETURNS trigger AS $orders_history$

    BEGIN
        -- if the operation is a delete
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO orders_history SELECT 'DELETE', OLD.*;
        -- if the operation is an update
        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO orders_history SELECT 'UPDATE', NEW.*;
        -- if the operation is an insert
        ELSIF (TG_OP = 'INSERT') THEN
            INSERT INTO orders_history SELECT 'INSERT', NEW.*;
        END IF;
        RETURN NULL;
    END;

$orders_history$ 
LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER orders_history_trigger
    AFTER INSERT OR UPDATE OR DELETE ON orders
        FOR EACH ROW EXECUTE PROCEDURE orders_history_trigger_function();