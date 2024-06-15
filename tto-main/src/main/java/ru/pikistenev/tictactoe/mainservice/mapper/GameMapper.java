package ru.pikistenev.tictactoe.mainservice.mapper;

import java.util.ArrayList;
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
    @Mapping(source = "game", target = "prettyBoard", qualifiedByName = "prettyBoardMap")
    GameResponse toGameResponse(Game game);


    @Named("boardMap")
    default List<String> boardSetFreeCell(Game game) {
        List<UserType> userTypeList = new Board(game).getBoard();
        return userTypeList.stream().map(userType -> {
            if (userType.equals(UserType.AI)) {
                return game.getAiSymbol();
            } else if (userType.equals(UserType.USER)) {
                return game.getUserSymbol();
            }
            return "";
        }).toList();
    }

    @Named("prettyBoardMap")
    default List<String> prettyBoardSetFreeCell(Game game) {
        List<UserType> userTypeList = new Board(game).getBoard();
        List<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < userTypeList.size(); i++) {
            if (i == 3 || i == 6) {
                result.add(line.toString());
                line = new StringBuilder();
            }

            if (userTypeList.get(i).equals(UserType.AI)) {
                line.append(game.getAiSymbol());
            } else if (userTypeList.get(i).equals(UserType.USER)) {
                line.append(game.getUserSymbol());
            } else {
                line.append("_");
            }

        }
        result.add(line.toString());
        return result;
    }
}
