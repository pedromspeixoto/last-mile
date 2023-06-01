CREATE TABLE IF NOT EXISTS qrtz_calendars
(
    sched_name varchar(120) not null,
    calendar_name  varchar(200) not null,
    calendar bytea not null,
    primary key (sched_name,calendar_name)
);