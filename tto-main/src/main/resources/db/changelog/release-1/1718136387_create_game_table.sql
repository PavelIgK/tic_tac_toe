--liquibase formatted sql

--changeset kistenev-pi:create_game_table
create table game (
    id UUID primary key,
    start_date DATE
    )

--rollback drop table game;