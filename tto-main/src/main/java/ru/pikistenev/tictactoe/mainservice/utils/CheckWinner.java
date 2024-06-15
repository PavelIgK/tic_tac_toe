package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;
import ru.pikistenev.tictactoe.mainservice.model.Game;

/**
 * Определегние победителя игры.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckWinner {

    /**
     * Проверяем есть ли победитель.
     *
     * @param game Игра для проверки.
     */
    public void check(Game game) {
        log.debug("Проверяем появился ли победитель в игре. gameId = {}", game.getId());

        //Если ходов меньше 5 то нет смысла ничего проверять еще никто не мог выиграть
        if (game.getSteps().size() < 5) {
            log.debug("В игре ходов меньше 5, не будем ничего проверять");
            return;
        }

        Set<Integer> aiPositions = new HashSet<>();
        Set<Integer> userPositions = new HashSet<>();

        game.getSteps().forEach(step -> {
            if (step.getIsUserStep()) {
                userPositions.add(step.getCell());
            } else {
                aiPositions.add(step.getCell());
            }
        });

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
                game.setWinner(Winner.AI);
                return;
            }

            //Если текущая выигрышная комбинация содержится в ходах пользователя - пользователь победил
            if (userPositions.containsAll(combination)) {
                log.debug("Победил пользователь, с комбинацией = {}", combination);
                game.setWinner(Winner.USER);
                return;
            }

            log.debug("Для комбинации {}. Победитель не определен идем дальше", combination);
        }

        //Если победитель не определен, а все ячейки заполнены - ничья.
        if (game.getWinner() == null && game.getSteps().size() == 9) {
            log.debug("Ничья.");
            game.setWinner(Winner.DRAW);
        }
        log.debug("Проверка наличия победителя завершена. gameId = {}", game.getId());
    }
}
