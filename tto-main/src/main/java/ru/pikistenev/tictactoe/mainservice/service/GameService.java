package ru.pikistenev.tictactoe.mainservice.service;


import java.util.UUID;
import ru.pikistenev.tictactoe.mainservice.model.Game;

/**
 * Сервис для игры.ы
 */
public interface GameService {

    /**
     * Запустить новую игру.
     *
     * @param isStartUser true - начинает пользователь, false - начинает машина.
     * @return Новая игра.
     */
    Game startGame(boolean isStartUser);

    /**
     * Обработать ход пользователя.
     *
     * @param gameId UUID игры.
     * @param cell ячейка в которую пользователь хочет сделать ход от 0 до 8
     * @return Состояние игры после хода пользователя.
     */
    Game userStep(UUID gameId, int cell);

    /**
     * Получить текущее состояние игры.
     *
     * @param gameId UUID игры.
     * @return Состояние игры.
     */
    Game getBoard(UUID gameId);

    /**
     * Запрос на отмену последнего хода
     *
     * @param gameId UUID игры.
     */
    void cancelStep(UUID gameId);
}
