
    create table archive_history (
        batch_history_id bigint,
        timestamp datetime(6) not null,
        archive_id varchar(255),
        archive_url varchar(255),
        case_id varchar(255) not null,
        document_id varchar(255) not null,
        document_name varchar(255),
        document_type varchar(255),
        archive_status enum ('COMPLETED','NOT_COMPLETED') not null,
        primary key (case_id, document_id)
    ) engine=InnoDB;

    create table batch_history (
        end date not null,
        start date not null,
        id bigint not null auto_increment,
        timestamp datetime(6) not null,
        archive_status enum ('COMPLETED','NOT_COMPLETED') not null,
        batch_trigger enum ('MANUAL','SCHEDULED') not null,
        primary key (id)
    ) engine=InnoDB;

    alter table if exists archive_history 
       add constraint FK10xve5gt1f8fjg37g44o9aw4f 
       foreign key (batch_history_id) 
       references batch_history (id);
