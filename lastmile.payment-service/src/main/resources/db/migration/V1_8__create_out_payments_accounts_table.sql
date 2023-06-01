CREATE TABLE IF NOT EXISTS out_payments_accounts (
    id serial PRIMARY KEY,
    out_payment_account_identification varchar(255) not null,
    account_holder_name varchar(255) not null,
    account_iban varchar(255) not null,
    account_email varchar(255) not null,
    account_phone_number varchar(255) not null,
    account_bank_account_country_code varchar(255) not null,
    account_payment_type varchar(255) not null,
    external_entity varchar(255) not null,
    external_entity_identification varchar(255),
    status varchar(255) not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(out_payment_account_identification)
);