package ru.pikistenev.tictactoe.mainservice.controller;



import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pikistenev.tictactoe.dto.GameResponse;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.exception.ValidationException;
import ru.pikistenev.tictactoe.mainservice.mapper.GameMapper;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.service.GameService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/${tto.api.endpoint}/")
public class GameController {
    private final GameService gameService;
    private final GameMapper gameMapper;
    private final TtoConfig ttoConfig;

    @GetMapping
    public GameResponse startGame(@RequestParam(required = false, defaultValue = "true") Boolean userStart, HttpSession session) {
        log.debug("[GameController][startGame] Запрос на новую игру. Первый ход пользователя = {}", userStart);
        Game game = gameService.startGame(userStart);
        session.setAttribute(ttoConfig.getAttrName(), game.getId());
        return gameMapper.toGameResponse(game);
    }

    @PatchMapping("/{cell}")
    public GameResponse userStep(@PathVariable @Min(0) @Max(8) Integer cell,
            HttpSession session) {
        log.debug("[GameController][userStep] Ход пользователя. gameId = {}, cell = {}", session.getAttribute(
                ttoConfig.getAttrName()), cell);
        return gameMapper.toGameResponse(gameService.userStep(getGameIdFromSession(session), cell));
    }

    @GetMapping("/board")
    public GameResponse getBoard(HttpSession session) {
        log.debug("[GameController][getBoard] Запрос доски. gameId = {}", session.getAttribute(ttoConfig.getAttrName()));
        return gameMapper.toGameResponse(gameService.getBoard(getGameIdFromSession(session)));
    }


    private UUID getGameIdFromSession(HttpSession session) {
        UUID gameId = (UUID) session.getAttribute(ttoConfig.getAttrName());
        if (gameId == null) {
            throw new ValidationException("В сесии нет параметра с идентификатором игры.");
        }
        return gameId;
    }

}
