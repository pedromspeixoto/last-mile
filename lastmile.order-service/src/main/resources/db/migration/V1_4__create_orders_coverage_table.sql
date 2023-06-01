CREATE TABLE IF NOT EXISTS orders_coverage (
    id serial PRIMARY KEY,
    country varchar(255) not null,
    city varchar(255) not null,
    latitude float not null,
    longitude float not null,
    radius integer not null
);