--liquibase formatted sql

--changeset fornit:create_project_table
create table if not exists project
(
    id         uuid primary key     default gen_random_uuid(),
    version    integer     not null default 1,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    config     text
);
--rollback drop table if exists project

--changeset fornit:create_project_idx
create unique index if not exists project_idx on project (id, version);
--rollback drop index if exists project_idx;
