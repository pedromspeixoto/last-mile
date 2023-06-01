-- base fare
INSERT INTO pricing_base_configuration (id, execution_order, fee_name, fee_type, fee_value, fee_cap, reference_column, reference_column_value)
VALUES (1, 10, 'BASE_FARE', 'FIXED', 1.5, null, null, null);

-- distance fare
INSERT INTO pricing_base_configuration (id, execution_order, fee_name, fee_type, fee_value, fee_cap, reference_column, reference_column_value)
VALUES (2, 20, 'DISTANCE_FARE_METER', 'MULTIPLIER', 0.000065, 4, 'estimatedDistance', null);

-- order value fare
INSERT INTO pricing_base_configuration (id, execution_order, fee_name, fee_type, fee_value, fee_cap, reference_column, reference_column_value)
VALUES (3, 30, 'ORDER_VALUE_FARE', 'MULTIPLIER', 0.15, 4, 'orderValue', null);

-- fragile fare
INSERT INTO pricing_base_configuration (id, execution_order, fee_name, fee_type, fee_value, fee_cap, reference_column, reference_column_value)
VALUES (4, 40, 'FRAGILE_FARE', 'EQUALS', 1, null, 'orderType', 'FRAGILE');

-- priority fare
INSERT INTO pricing_base_configuration (id, execution_order, fee_name, fee_type, fee_value, fee_cap, reference_column, reference_column_value)
VALUES (5, 50, 'PRIORITY_FARE', 'EQUALS', 0.5, null, 'priority', 'HIGH');

-- surge fare
INSERT INTO pricing_base_configuration (id, execution_order, fee_name, fee_type, fee_value, fee_cap, reference_column, reference_column_value)
VALUES (6, 900, 'SURGE_FARE', 'SURGE', null, null, null, null);