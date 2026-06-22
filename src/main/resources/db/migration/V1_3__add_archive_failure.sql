create table archive_failure (
    id bigint not null auto_increment,
    batch_history_id bigint not null,
    timestamp datetime(6) not null,
    case_id varchar(255),
    document_id varchar(255),
    municipality_id varchar(255),
    document_name varchar(255),
    message varchar(255),
    detail longtext,
    failure_category varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create index archive_failure_batch_history_id_idx
    on archive_failure (batch_history_id);

create index archive_failure_municipality_id_idx
    on archive_failure (municipality_id);

create index archive_failure_failure_category_idx
    on archive_failure (failure_category);
