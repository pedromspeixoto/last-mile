CREATE TABLE IF NOT EXISTS twilio_phone_numbers (
    id serial PRIMARY KEY,
    twilio_phone_number_sid varchar(255) not null,
    twilio_phone_number varchar(255) not null,
    in_use boolean,
    UNIQUE(twilio_phone_number_sid),
    UNIQUE(twilio_phone_number)
);