-- local
INSERT INTO order_properties (environment, property, value)
VALUES ('local', 'drivers_percentage_fee', '0.75');

INSERT INTO order_properties (environment, property, value)
VALUES ('local', 'current_surge_fee', '1');

INSERT INTO order_properties (environment, property, value)
VALUES ('local', 'average_driver_assign_time', '300');

INSERT INTO order_properties (environment, property, value)
VALUES ('local', 'order_timeout_value', '180');

-- dev
INSERT INTO order_properties (environment, property, value)
VALUES ('dev', 'drivers_percentage_fee', '0.75');

INSERT INTO order_properties (environment, property, value)
VALUES ('dev', 'current_surge_fee', '1');

INSERT INTO order_properties (environment, property, value)
VALUES ('dev', 'average_driver_assign_time', '300');

INSERT INTO order_properties (environment, property, value)
VALUES ('dev', 'order_timeout_value', '180');