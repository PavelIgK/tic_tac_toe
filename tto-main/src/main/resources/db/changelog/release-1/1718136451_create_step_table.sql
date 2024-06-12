--liquibase formatted sql

--changeset kistenev-pi:create_step_table
create table step
(
    id           UUID primary key,
    created      TIMESTAMP WITH TIME ZONE,
    updated      TIMESTAMP WITH TIME ZONE,
    game_id      UUID NOT NULL,
    cell         INT4,
    is_user_step BOOL
)

--rollback drop table step;