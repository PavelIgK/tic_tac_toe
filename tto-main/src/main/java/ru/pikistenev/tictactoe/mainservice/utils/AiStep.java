package ru.pikistenev.tictactoe.mainservice.utils;


import ru.pikistenev.tictactoe.mainservice.model.Game;

/**
 * Интерфейс для определения ячейки куда ходит машина.
 */
public interface AiStep {

    /**
     * Получить ячейку куда ходит машина.
     *
     * @param game Текущее состояние игры.
     * @return номер ячейки от 0 до 8.
     */
    int findAiStepCell(Game game);
}
