CREATE TABLE IF NOT EXISTS customers (
    id serial PRIMARY KEY,
    customer_identification varchar(255) not null,
    name varchar(255) not null,
    public_name varchar(255) not null,
    customer_email varchar(255),
    customer_phone_number varchar(255),
    customer_website varchar(255),
    nif varchar(255) not null,
    active_address_id varchar(255),
    active_billing_address_id varchar(255),
    active_payment_details_id varchar(255),
    api_key varchar(255) not null,
    private_key varchar(255) not null,
    customer_callback_url varchar(255),
    status varchar(255) not null,
    entity_validated boolean not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(customer_identification)
);