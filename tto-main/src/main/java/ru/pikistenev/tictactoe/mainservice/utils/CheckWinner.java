package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;
import ru.pikistenev.tictactoe.mainservice.model.Game;

@Component
@RequiredArgsConstructor
public class CheckWinner {

    public void check(Game game) {
        String[] board = game.getBoard();
        List<Integer> aiPosition = new ArrayList<>();
        List<Integer> userPositions = new ArrayList<>();
        Integer cellNotNull = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                continue;
            }

            if (board[i].equals(game.getAiSymbol())) {
                aiPosition.add(i);
            } else {
                userPositions.add(i);
            }
            cellNotNull++;
        }
        if (cellNotNull < 5) {
            return;
        }

        List<List<Integer>> winnerCombination = new ArrayList<>();
        winnerCombination.add(List.of(0, 1, 2));
        winnerCombination.add(List.of(3, 4, 5));
        winnerCombination.add(List.of(6, 7, 8));
        winnerCombination.add(List.of(0, 3, 6));
        winnerCombination.add(List.of(1, 4, 7));
        winnerCombination.add(List.of(2, 5, 8));
        winnerCombination.add(List.of(0, 4, 8));
        winnerCombination.add(List.of(2, 4, 6));

        winnerCombination.forEach(combination -> {
            Winner winner = game.getWinner();
            if (winner != null) {
                return;
            }

            if (aiPosition.containsAll(combination)) {
                game.setWinner(Winner.AI);
                return;
            }

            if (userPositions.containsAll(combination)) {
                game.setWinner(Winner.USER);
            }
        });

        if (game.getWinner() == null && cellNotNull.equals(9)) {
            game.setWinner(Winner.DRAW);
        }
    }
}
