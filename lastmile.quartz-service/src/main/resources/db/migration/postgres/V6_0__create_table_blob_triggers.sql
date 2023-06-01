CREATE TABLE IF NOT EXISTS qrtz_blob_triggers
(
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    blob_data bytea null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
        references qrtz_triggers(sched_name,trigger_name,trigger_group)
);