CREATE TABLE IF NOT EXISTS warehouses (
    id serial PRIMARY KEY,
    warehouse_identification varchar(255) not null,
    customer_identification varchar(255) not null references customers(customer_identification),
    name varchar(255),
    description varchar(255),
    address_id varchar(255),
    latitude float not null,
    longitude float not null,
    status varchar(255) not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(warehouse_identification)
);