package ru.pikistenev.tictactoe.mainservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.pikistenev.tictactoe.mainservice.dto.GameResponse;
import ru.pikistenev.tictactoe.mainservice.model.Game;

/**
 * Маппер для игры.
 */

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(source = "game", target = "board", qualifiedByName = "board")
    GameResponse toGameResponse(Game game);


    @Named("board")
    default String[] boardSetFreeCell(Game game) {
        String[] board = game.getBoard();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
             board[i] = "";
            }
        }
        return board;
    }
}
