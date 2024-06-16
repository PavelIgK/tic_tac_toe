package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.pikistenev.tictactoe.mainservice.enums.UserType;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;

/**
 * Доска формируемая по игре.
 */
@Slf4j
public class Board {

    @Getter
    private final List<UserType> board;

    public Board(Game game) {
        board = new ArrayList<>(List.of(UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE));
        if (game.getSteps().isEmpty()) {
            return;
        }

        for (Step step : game.getSteps()) {
            int cell = step.getCell();
            UserType userType = step.isUserStep() ? UserType.USER : UserType.AI;
            board.set(cell, userType);
        }
    }


    /**
     * Получаем свободные ячейки.
     *
     * @return список свободных ячеек.
     */
    public List<Integer> freeCells() {
        ArrayList<Integer> freeCells = new ArrayList<>();
        List<UserType> boardCells = this.board;
        for (int i = 0; i < boardCells.size(); i++) {
            if (boardCells.get(i).equals(UserType.FREE)) {
                freeCells.add(i);
            }
        }
        return freeCells;
    }

    /**
     * Проверка есть ли победитель.
     *
     * @return Победитель или ничья.
     */
    public Winner checkWinner() {
        log.debug("Проверяем появился ли победитель в игре.");

        //Если ходов меньше 5 то нет смысла ничего проверять еще никто не мог выиграть
        if (board.stream().filter(userType -> userType != UserType.FREE).count() < 5) {
            log.debug("В игре ходов меньше 5, не будем ничего проверять");
            return null;
        }

        Set<Integer> aiPositions = new HashSet<>();
        Set<Integer> userPositions = new HashSet<>();

        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).equals(UserType.USER)) {
                userPositions.add(i);
            } else if (board.get(i).equals(UserType.AI)) {
                aiPositions.add(i);
            }
        }

        log.debug("Ходы пользователя = {}", userPositions);
        log.debug("Ходы машины = {}", aiPositions);

        //Соберем выигрышные вырианты
        List<Set<Integer>> winnerCombination = new ArrayList<>();
        winnerCombination.add(Set.of(0, 1, 2));
        winnerCombination.add(Set.of(3, 4, 5));
        winnerCombination.add(Set.of(6, 7, 8));
        winnerCombination.add(Set.of(0, 3, 6));
        winnerCombination.add(Set.of(1, 4, 7));
        winnerCombination.add(Set.of(2, 5, 8));
        winnerCombination.add(Set.of(0, 4, 8));
        winnerCombination.add(Set.of(2, 4, 6));

        for (Set<Integer> combination : winnerCombination) {

            //Если текущая выигрышная комбинация содержится в ходах машины - она победила
            if (aiPositions.containsAll(combination)) {
                log.debug("Победила машина, с комбинацией = {}", combination);
                return Winner.AI;
            }

            //Если текущая выигрышная комбинация содержится в ходах пользователя - пользователь победил
            if (userPositions.containsAll(combination)) {
                log.debug("Победил пользователь, с комбинацией = {}", combination);
                return Winner.USER;
            }

            log.debug("Для комбинации {}. Победитель не определен идем дальше", combination);
        }

        //Если победитель не определен, а все ячейки заполнены - ничья.
        if (board.stream().noneMatch(UserType.FREE::equals)) {
            log.debug("Ничья.");
            return Winner.DRAW;
        }
        return null;
    }
}