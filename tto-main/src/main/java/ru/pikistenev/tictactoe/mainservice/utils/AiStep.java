package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.enums.UserType;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;

/**
 * Подбор следующего хода уровень.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiStep {

    private Board board;

    /**
     * Получить ячейку куда ходит машина.
     *
     * @param game Текущее состояние игры.
     * @return номер ячейки от 0 до 8.
     */
    public int findAiStepCell(Game game) {
        log.debug("Запускаем поиск ячейки для хода машины. gameId = {}", game.getId());
        //Заготовка под разное определение следующего хода
        board = new Board(game);
        switch (game.getLevel()) {
            case EASY -> {
                return veryEasyGame(game);
            }
            case MEDIUM -> {
                return mediumGame(game);
            }
            case HARD -> {
                return hardGame(game);
            }
        }
        return veryEasyGame(game);
    }

    /**
     * Реализация самого простого подбора следующего хода. Рандомно из свободных ячеек.
     *
     * @param game Игра
     * @return Номер ячейки.
     */
    private Integer veryEasyGame(Game game) {
        log.debug("Определяем ход машины по самому простому алгоритму. gameId = {}", game.getId());
        List<Integer> freeCells = board.freeCells();
        return freeCells.get(new Random().nextInt(freeCells.size()));
    }


    /**
     * Реализация среднего уровня игры.
     * Пока свободно больше 4 ячеек - машина подбирает лучший ход для себя.
     *
     * @param game Игра
     * @return Номер ячейки.
     */
    private Integer mediumGame(Game game) {
        log.debug("Определяем ход машины по средней сложности алгоритму. gameId = {}", game.getId());
        List<Integer> freeCells = board.freeCells();
        if (freeCells.size() > 4) {
            return hardGame(game);
        }
        return freeCells.get(new Random().nextInt(freeCells.size()));
    }

    /**
     * Самый сложный уровень.
     * Машина подбирает самый лучший ход для себя всегда.
     * По сути лучший исход для пользователя - ничья.
     *
     * @param game игра
     * @return Номер ячейки.
     */
    private Integer hardGame(Game game) {
        board = new Board(game);

        // Для ускорения работы. Если бот ходит первый - вернем рандомно одну из угловых(они самые ценные первым ходом).
        if (board.freeCells().size() == 9) {
            List<Integer> bestFirstCells = new ArrayList<>(List.of(0,2,6,8));
            return bestFirstCells.get(new Random().nextInt(4));
        }
        Step step = minimax(board, UserType.AI);
        return step.getCell();
    }


    /**
     * Функция минимакс для оценки ценности ячейки для компьютера.
     * Рекурсивно вызывает саму себя и определяет какая ячейка наберет больше всего очков.
     *
     * @param board доска для подбора.
     * @param userType кто ходит в этот момент.
     * @return Лучший ход.
     */
    private Step minimax(Board board, UserType userType) {
        List<Integer> freeCells = board.freeCells();
        if (board.checkWinner() != null) {
            return Step.builder().score(board.evaluate()).build();
        }

        List<Step> steps = new ArrayList<>();

        for (Integer freeCell : freeCells) {

            board.step(freeCell, userType);
            Step currentStep;
            Step score;
            if (userType.equals(UserType.AI)) {
                score = minimax(board, UserType.USER);
            } else {
                score = minimax(board, UserType.AI);
            }
            currentStep = Step.builder().cell(freeCell).score(score.getScore()).build();
            board.getBoard().set(freeCell, UserType.FREE);
            steps.add(currentStep);
        }

        Step best = null;
        if (UserType.AI.equals(userType)) {
            int minValue = Integer.MIN_VALUE;
            for (Step step : steps) {
                if (step.getScore() > minValue) {
                    best = step;
                    minValue = step.getScore();
                }
            }
        } else {
            int maxValue = Integer.MAX_VALUE;
            for (Step step : steps) {
                if (step.getScore() < maxValue) {
                    best = step;
                    maxValue = step.getScore();

                }
            }
        }
        return best;
    }
}
