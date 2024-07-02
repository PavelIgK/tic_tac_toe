--liquibase formatted sql

-- Таблица для хранения информации об играх через бота
--changeset kistenev-pi:create_bot_game_table
create table bot_game
(
    id            UUID                     NOT NULL,
    created       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated       TIMESTAMP WITH TIME ZONE NOT NULL,
    game_id       UUID                     NULL,
    game_step     VARCHAR(32)              NULL,
    chat_id       BIGINT                   NOT NULL,
    message_id    INT4                     NULL,
    is_user_start BOOL                     NULL,
    level         VARCHAR(16)              NULL,
    status        VARCHAR(16)              NULL,
    winner        VARCHAR(16)              NULL,

    CONSTRAINT pk_bot_game PRIMARY KEY (id)
);

COMMENT ON TABLE bot_game IS 'Таблица для хранения информации об играх';
COMMENT ON COLUMN bot_game.id IS 'Уникальный идентификатор записи в таблице';
COMMENT ON COLUMN bot_game.created IS 'Дата и время создания записи';
COMMENT ON COLUMN bot_game.updated IS 'Дата и время обновления записи';
COMMENT ON COLUMN bot_game.game_id IS 'Id игры';
COMMENT ON COLUMN bot_game.chat_id IS 'Id чата в котором идет игра';
COMMENT ON COLUMN bot_game.message_id IS 'Id сообщения с игрой';
COMMENT ON COLUMN bot_game.is_user_start IS 'Признак начала игры пользователем';
COMMENT ON COLUMN bot_game.level IS 'Уровень сложности игры';
COMMENT ON COLUMN bot_game.status IS 'Статус игры';
COMMENT ON COLUMN bot_game.winner IS 'Победитель';
--rollback DROP TABLE bot_game;