package ru.pikistenev.tictactoe.mainservice.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.pikistenev.tictactoe.mainservice.dto.GameResponse;
import ru.pikistenev.tictactoe.mainservice.enums.UserType;
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
        List<UserType> userTypeList = new Board().getBoard(game);
        return userTypeList.stream().map(userType -> {
            if (userType.equals(UserType.AI)) {
                return game.getAiSymbol();
            } else if (userType.equals(UserType.USER)) {
                return game.getUserSymbol();
            }
            return "";
        }).toList();
    }
}
