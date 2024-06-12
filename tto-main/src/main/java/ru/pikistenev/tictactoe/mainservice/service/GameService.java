package ru.pikistenev.tictactoe.mainservice.service;


import java.util.UUID;
import ru.pikistenev.tictactoe.mainservice.model.Game;

public interface GameService {

    Game startGame(Boolean isStartUser);

    Game userStep(UUID gameId, Integer cell);

    Game getBoard(UUID gameId);
}
