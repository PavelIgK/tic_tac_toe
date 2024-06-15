package ru.pikistenev.tictactoe.mainservice.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;

@Slf4j
@Component
@AllArgsConstructor
public class Board {

    public List<String> getBoard(Game game) {
        List<String> boardMap = new ArrayList<>(List.of("", "", "", "", "", "", "", "", ""));
        if (game.getSteps().isEmpty()) {
            return boardMap;
        }

        for (Step step : game.getSteps()) {
            log.info(boardMap.toString());
            Integer cell = step.getCell();
            String symbol = step.getIsUserStep() ? game.getUserSymbol().toString() : game.getAiSymbol().toString();
            log.info(cell + " " + symbol);
            boardMap.set(cell, symbol);
            log.info(boardMap.toString());
        }
        return boardMap;
    }

}