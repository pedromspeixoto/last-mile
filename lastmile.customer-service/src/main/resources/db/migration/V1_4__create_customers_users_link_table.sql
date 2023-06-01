CREATE TABLE IF NOT EXISTS customers_users_link (
    id serial PRIMARY KEY,
    customer_identification varchar(255) not null,
    user_identification varchar(255) not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);