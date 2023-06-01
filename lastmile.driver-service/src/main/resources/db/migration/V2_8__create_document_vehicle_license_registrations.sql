CREATE TABLE IF NOT EXISTS document_vehicle_license_registrations (
    id serial PRIMARY KEY,
    vehicle_license_reg_identification varchar(255) not null,
    vehicle_identification varchar(255) not null REFERENCES drivers_vehicles(vehicle_identification),
    document_front_file_id varchar(255),
    document_back_file_id varchar(255),
    validated boolean,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(vehicle_license_reg_identification)
);