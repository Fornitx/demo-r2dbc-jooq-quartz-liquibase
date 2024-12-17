--liquibase formatted sql

--changeset fornit:create_back_service_table
create table if not exists back_service
(
    id             uuid primary key     default gen_random_uuid(),
    service_domain varchar(50) not null
);
--rollback drop table if exists back_service

--changeset fornit:create_back_method_table
create table if not exists back_method
(
    id          uuid primary key     default gen_random_uuid(),
    service_id  uuid not null references back_service (id),
    method_path varchar(50) not null
);
--rollback drop table if exists back_method
