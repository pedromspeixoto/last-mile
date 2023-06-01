CREATE TABLE IF NOT EXISTS payments (
    id serial PRIMARY KEY,
    payment_identification varchar(255) not null,
    requester_entity_identification varchar(255) not null,
    requester_entity_type varchar(255) not null,
    transaction_identification varchar(255) not null,
    payment_details_id varchar(255) not null,
    payment_value numeric not null,
    payment_type varchar(255) not null,
    external_payment_identification varchar(255),
    status varchar(255) not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(payment_identification)
);