package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.enums.UserType;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;

@Slf4j
@Component
@AllArgsConstructor
public class Board {

    public List<UserType> getBoard(Game game) {
        List<UserType> board = new ArrayList<>(List.of(UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE,
                UserType.FREE));
        if (game.getSteps().isEmpty()) {
            return board;
        }

        for (Step step : game.getSteps()) {
            Integer cell = step.getCell();
            UserType userType = step.getIsUserStep() ? UserType.USER : UserType.AI;
            board.set(cell, userType);
        }
        return board;
    }


    public List<Integer> freeCells(Game game) {
        ArrayList<Integer> freeCells = new ArrayList<>();
        List boardCells = getBoard(game);
        for (int i = 0; i < boardCells.size(); i++) {
            if (boardCells.get(i).equals(UserType.FREE)) {
                freeCells.add(i);
            }
        }
        return freeCells;
    }

}