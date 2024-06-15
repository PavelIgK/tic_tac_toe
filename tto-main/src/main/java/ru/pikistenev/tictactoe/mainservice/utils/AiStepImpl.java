package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.model.Game;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiStepImpl implements AiStep {

    private final Board board;

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
        ArrayList<Integer> freeCells = new ArrayList<>();
        List boardCells = board.getBoard(game);
        for (int i = 0; i < boardCells.size(); i++) {
            if (boardCells.get(i).equals("")) {
                freeCells.add(i);
            }
        }
        return freeCells.get(new Random().nextInt(freeCells.size()));
    }
}
