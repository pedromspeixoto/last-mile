CREATE TABLE IF NOT EXISTS invoices (
    id serial PRIMARY KEY,
    invoice_identification varchar(255) not null,
    payment_identification varchar(255) not null,
    entity_identification varchar(255) not null,
    entity_type varchar(255) not null,
    invoice_client_name varchar(255),
    invoice_fiscal_number varchar(255) not null,
    invoice_full_address varchar(255),
    invoice_zip_code varchar(255),
    invoice_url varchar(255) not null,
    external_entity varchar(255) not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(invoice_identification)
);