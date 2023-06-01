-- local
INSERT INTO account_properties (environment, property, value)
VALUES ('local', 'bypass_external_validation', 'N');

-- dev
INSERT INTO account_properties (environment, property, value)
VALUES ('dev', 'bypass_external_validation', 'Y');