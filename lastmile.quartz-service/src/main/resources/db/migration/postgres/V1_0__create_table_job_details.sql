CREATE TABLE IF NOT EXISTS qrtz_job_details
(
    sched_name varchar(120) not null,
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    description varchar(250) null,
    job_class_name   varchar(250) not null,
    is_durable bool not null,
    is_nonconcurrent bool not null,
    is_update_data bool not null,
    requests_recovery bool not null,
    job_data bytea null,
    primary key (sched_name,job_name,job_group)
);