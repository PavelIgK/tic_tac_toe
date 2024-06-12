package ru.pikistenev.tictactoe.mainservice.mapper;

import org.mapstruct.Mapper;
import ru.pikistenev.tictactoe.dto.GameResponse;
import ru.pikistenev.tictactoe.mainservice.model.Game;

@Mapper(componentModel = "spring")
public interface GameMapper {

    //@Mapping(source = "game", target = "board", qualifiedByName = "board")
    GameResponse toGameResponse(Game game);


//    @Named("board")
//    default String[] locationToLocationDto(Game game) {
//        GameResponse gameResponse = new GameResponse();
//        gameResponse.setId(game.getId());
//        String[] board = new String[]{"","","","","","","","",""};
//        game.getSteps().stream().forEach(step -> {
//            Integer cell = step.getCell();
//            String symbol = step.getIsUserStep() ? game.getUserSymbol() : game.getAiSymbol();
//            board[cell] = symbol;
//        });
//        gameResponse.setBoard(board);
//        return board;
//    }
}
