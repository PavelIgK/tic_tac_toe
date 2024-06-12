--liquibase formatted sql

--changeset kistenev-pi:create_game_table
create table game
(
    id            UUID primary key,
    created       TIMESTAMP WITH TIME ZONE,
    updated       TIMESTAMP WITH TIME ZONE,
    is_user_start BOOL,
    ai_symbol     VARCHAR(16),
    user_symbol   VARCHAR(16),
    status        VARCHAR(16),
    winner        VARCHAR(16)
)

--rollback drop table game;