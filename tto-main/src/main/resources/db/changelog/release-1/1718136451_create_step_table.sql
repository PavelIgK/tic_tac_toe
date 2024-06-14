--liquibase formatted sql

-- Таблица с историей шагов по играм
--changeset kistenev-pi:create_step_table
create table step
(
    id           UUID NOT NULL,
    created      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated      TIMESTAMP WITH TIME ZONE NOT NULL,
    game_id      UUID NOT NULL,
    cell         INT4 NOT NULL,
    is_user_step BOOL NOT NULL,

    CONSTRAINT pk_step PRIMARY KEY (id),
    CONSTRAINT fk_step_id_game FOREIGN KEY (game_id) REFERENCES game (id)
);

COMMENT ON TABLE step IS 'Таблица для хранения информации об играх';
COMMENT ON COLUMN step.id IS 'Уникальный идентификатор записи в таблице';
COMMENT ON COLUMN step.game_id IS 'Уникальный идентификатор записи объекта "Игра"';
COMMENT ON COLUMN step.created IS 'Дата и время создания записи';
COMMENT ON COLUMN step.updated IS 'Дата и время обновления записи';
COMMENT ON COLUMN step.cell IS 'Номер ячейки';
COMMENT ON COLUMN step.is_user_step IS 'Признак хода пользователя';
--rollback DROP TABLE step;