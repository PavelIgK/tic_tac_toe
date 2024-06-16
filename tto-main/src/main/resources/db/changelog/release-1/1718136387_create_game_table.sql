--liquibase formatted sql

-- Таблица для хранения информации об играх
--changeset kistenev-pi:create_game_table
create table game
(
    id            UUID NOT NULL,
    created       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated       TIMESTAMP WITH TIME ZONE NOT NULL,
    is_user_start BOOL NOT NULL,
    ai_symbol     VARCHAR(16) NOT NULL,
    user_symbol   VARCHAR(16) NOT NULL ,
    level         VARCHAR(16) NULL,
    status        VARCHAR(16) NOT NULL,
    winner        VARCHAR(16) NULL,

    CONSTRAINT pk_game PRIMARY KEY (id)
);

COMMENT ON TABLE game IS 'Таблица для хранения информации об играх';
COMMENT ON COLUMN game.id IS 'Уникальный идентификатор записи в таблице';
COMMENT ON COLUMN game.created IS 'Дата и время создания записи';
COMMENT ON COLUMN game.updated IS 'Дата и время обновления записи';
COMMENT ON COLUMN game.is_user_start IS 'Признак начала игры пользователем';
COMMENT ON COLUMN game.ai_symbol IS 'Символ которым играет машина';
COMMENT ON COLUMN game.user_symbol IS 'Символ которым играет пользователь';
COMMENT ON COLUMN game.level IS 'Уровень сложности игры';
COMMENT ON COLUMN game.status IS 'Статус игры';
COMMENT ON COLUMN game.winner IS 'Победитель';
--rollback DROP TABLE game;