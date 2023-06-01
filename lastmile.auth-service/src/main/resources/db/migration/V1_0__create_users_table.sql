CREATE TABLE IF NOT EXISTS users (
    id serial PRIMARY KEY,
    user_identification varchar(255) not null,
    username varchar(255) not null,
    password varchar(255) not null,
    activated boolean default true,
    activation_key varchar(255),
    reset_password_key varchar(255),
    authorities varchar(255) not null
);