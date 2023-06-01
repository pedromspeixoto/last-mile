CREATE TABLE IF NOT EXISTS driver_license_categories (
    id serial PRIMARY KEY,
    driver_license_category_identification varchar(255) NOT NULL,
    driver_license_identification varchar(255) NOT NULL REFERENCES document_driver_licenses(driver_license_identification),
    category varchar(255) NOT NULL,
    category_issue_date date,
    category_expiry_date date,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    UNIQUE(driver_license_category_identification)
);