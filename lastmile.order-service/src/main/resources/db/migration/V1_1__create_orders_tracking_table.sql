CREATE TABLE IF NOT EXISTS orders_tracking (
    id serial PRIMARY KEY,
    order_identification varchar(255) not null,
    assigned_driver varchar(255),
    order_action varchar(255),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);