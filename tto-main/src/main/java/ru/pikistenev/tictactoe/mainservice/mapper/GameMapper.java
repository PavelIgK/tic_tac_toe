package ru.pikistenev.tictactoe.mainservice.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.pikistenev.tictactoe.mainservice.dto.GameResponse;
import ru.pikistenev.tictactoe.mainservice.utils.Board;
import ru.pikistenev.tictactoe.mainservice.model.Game;

/**
 * Маппер для игры.
 */

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(source = "game", target = "board", qualifiedByName = "boardMap")
    GameResponse toGameResponse(Game game);


    @Named("boardMap")
    default List<String> boardSetFreeCell(Game game) {
        return new Board().getBoard(game);

    }
}
