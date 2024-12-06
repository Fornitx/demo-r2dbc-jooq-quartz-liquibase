--liquibase formatted sql

--changeset fornit:create_sdk_context_table
create table if not exists sdk_context
(
    id         uuid primary key     default gen_random_uuid(),
    version    integer     not null default 1,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    msg        varchar(50) not null,
    status     varchar(10)
        constraint status_enum check (status in ('ONE', 'TWO', 'THREE')),
    rules      jsonb,
    config     jsonb
);
--rollback drop table if exists sdk_context

--changeset fornit:create_sdk_context_idx
create unique index if not exists sdk_context_idx on sdk_context (id, version);
--rollback drop index if exists sdk_context_idx;
