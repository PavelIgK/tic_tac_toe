package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.model.Game;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiStepImpl implements AiStep {


    @Override
    public Integer findAiStepCell(Game game) {
        log.debug("[AiStepImpl][findAiStepCell] Запускаем поиск ячейки для хода машины. gameId = {}", game.getId());

        //Заготовка под разное определение следующего хода
        return veryEasyGame(game);
    }

    /**
     * Реализация самого простого подбора следующего хода.
     * Рандомно из свободных ячеек.
     *
     * @param game Игра
     * @return Номер ячейки.
     */
    private Integer veryEasyGame(Game game) {
        log.debug("[AiStepImpl][veryEasyGame] Определяем ход машины по самому простому алгоритму. gameId = {}", game.getId());
        String[] board = game.getBoard();
        ArrayList<Integer> freeCells = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                freeCells.add(i);
            }
        }
        return freeCells.get(new Random().nextInt(freeCells.size()));
    }
}
