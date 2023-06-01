CREATE TABLE IF NOT EXISTS drivers_vehicles (
    id serial PRIMARY KEY,
    vehicle_identification varchar(255) not null,
    driver_identification varchar(255) not null REFERENCES drivers(driver_identification),
    make varchar(255) not null,
    model varchar(255) not null,
    category varchar(255),
    year int not null,
    vehicle_type varchar(255) not null,
    license_plate varchar(255) not null,
    vehicle_active boolean,
    entity_validated boolean not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(license_plate),
    UNIQUE(vehicle_identification)
);