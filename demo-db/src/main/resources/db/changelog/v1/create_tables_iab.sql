--liquibase formatted sql

--changeset fornit:create_iab_service_table
create table if not exists iab_service
(
    id         uuid primary key     default gen_random_uuid(),
    domain varchar(50) not null
);
--rollback drop table if exists iab_service

--changeset fornit:create_iab_method_table
create table if not exists iab_method
(
    id         uuid primary key     default gen_random_uuid(),
    service_id uuid not null references iab_service (id),
    path varchar(50) not null
);
--rollback drop table if exists iab_method


