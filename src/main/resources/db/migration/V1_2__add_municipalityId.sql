alter table if exists archive_history
    add column municipality_id varchar(255);

alter table if exists batch_history
    add column municipality_id varchar(255);


-- Drop the existing constraint
alter table archive_history
    drop foreign key FK10xve5gt1f8fjg37g44o9aw4f;

-- Add the new constraint with the desired name
alter table if exists archive_history
    add constraint fk_archive_history_batch_history_id
        foreign key (batch_history_id)
            references batch_history (id);

create index archive_history_municipality_id_idx
    on archive_history (municipality_id);

create index archive_history_archive_status_idx
    on archive_history (archive_status);

create index batch_history_municipality_id_idx
    on batch_history (municipality_id);

create index batch_history_archive_status_idx
    on batch_history (archive_status);
