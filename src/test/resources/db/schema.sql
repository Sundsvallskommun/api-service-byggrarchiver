
    create table archive_history (
        batch_history_id bigint not null,
        timestamp datetime(6) not null,
        archive_id varchar(255),
        archive_url varchar(255),
        case_id varchar(255) not null,
        document_id varchar(255) not null,
        document_name varchar(255),
        document_type varchar(255),
        municipality_id varchar(255),
        archive_status varchar(255) not null,
        primary key (case_id, document_id)
    ) engine=InnoDB;

    create table batch_history (
        end date not null,
        start date not null,
        id bigint not null auto_increment,
        timestamp datetime(6) not null,
        municipality_id varchar(255),
        archive_status varchar(255) not null,
        batch_trigger varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create index archive_history_municipality_id_idx 
       on archive_history (municipality_id);

    create index archive_history_archive_status_idx 
       on archive_history (archive_status);

    create index batch_history_municipality_id_idx 
       on batch_history (municipality_id);

    create index batch_history_archive_status_idx 
       on batch_history (archive_status);

    alter table if exists archive_history 
       add constraint fk_archive_history_batch_history_id 
       foreign key (batch_history_id) 
       references batch_history (id);
