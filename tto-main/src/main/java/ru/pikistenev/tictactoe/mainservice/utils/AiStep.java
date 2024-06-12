package ru.pikistenev.tictactoe.mainservice.utils;


import ru.pikistenev.tictactoe.mainservice.model.Game;

public interface AiStep {
    Integer findAiStepCell(Game game);
}
