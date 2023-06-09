CREATE TABLE IF NOT EXISTS out_payments (
    id serial PRIMARY KEY,
    out_payment_identification varchar(255) not null,
    source_account_identification varchar(255),
    transaction_identification varchar(255) not null,
    requester_identification varchar(255) not null,
    requester_entity_type varchar(255) not null,
    requester_account_holder_name varchar(255),
    requester_iban varchar(255),
    requester_email varchar(255),
    requester_phone_number varchar(255),
    requester_bank_account_country_code varchar(255),
    payment_value numeric not null,
    payment_type varchar(255),
    payment_scheduled_date timestamp,
    external_payment_identification varchar(255),
    status varchar(255) not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(out_payment_identification)
);