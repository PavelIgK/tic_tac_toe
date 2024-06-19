package ru.pikistenev.tictactoe.mainservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.dto.GameResponse;
import ru.pikistenev.tictactoe.mainservice.enums.GameLevel;
import ru.pikistenev.tictactoe.mainservice.exception.ApiError;
import ru.pikistenev.tictactoe.mainservice.exception.ValidationException;
import ru.pikistenev.tictactoe.mainservice.mapper.GameMapper;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.service.GameService;

/**
 * Контроллер для игры.
 */

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/${tto.api.endpoint}/")
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;
    private final TtoConfig ttoConfig;

    /**
     * POST: Запрос на новую игру.
     *
     * @param userStart true - первый ход пользователя, false - первый ход машины
     * @param gameLevel уровень игры (EASY, MEDIUM, HARD)
     * @return Игра создана (status code 201)
     */
    @Operation(
            operationId = "startGame",
            summary = "Старт новой игры",
            description = "Если не передать параметр кто ходит первым, то по умолчанию первый ход пользователя.",
            tags = {"Игра"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Игра создана", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GameResponse.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "Некорректный уровень игры", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = IllegalArgumentException.class))
                    })
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse startGame(@RequestParam(required = false, defaultValue = "true") Boolean userStart,
            @RequestParam(required = false, defaultValue = "EASY") GameLevel gameLevel,
            HttpSession session) {
        log.debug("Запрос на новую игру. Первый ход пользователя = {}, уровень игры = {}", userStart, gameLevel);
        Game game = gameService.startGame(userStart, gameLevel);
        session.setAttribute(ttoConfig.getAttrName(), game.getId());
        return gameMapper.toGameResponse(game);
    }

    /**
     * PATCH: Запрос на ход пользователя.
     *
     * @param cell ячейка в которую хочет походить пользователь (от 0 до 8)
     * @return Ход сделан, текущее состояние доски (status code 200) или В сессии нет id игры (status code 400) или Игра
     * заверешна или данное поле недоступно(status code 403) или Игра с id переданным в сессии не найдена(status code
     * 404)
     */
    @Operation(
            operationId = "userStep",
            summary = "Ход пользователя",
            description = "Передается ячейка в которую хочет походить пользователь (от 0 до 8)",
            tags = {"Игра"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ход сделан, текущее состояние доски", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GameResponse.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "В сессии нет id игры", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "403", description = "Игра заверешна или данное поле недоступно", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Игра с id переданным в сессии не найдена", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    })
            }
    )
    @PatchMapping("/{cell}")
    public GameResponse userStep(@PathVariable @Min(0) @Max(8) int cell,
            HttpSession session) {
        log.debug("Ход пользователя. gameId = {}, cell = {}", session.getAttribute(
                ttoConfig.getAttrName()), cell);
        return gameMapper.toGameResponse(gameService.userStep(getGameIdFromSession(session), cell));
    }


    /**
     * GET: Запрос текущего состояния игры.
     *
     * @return Текущее состояние доски (status code 200) или В сессии нет id игры (status code 400) 404)
     */
    @Operation(
            operationId = "getBoard",
            summary = "Текущее состояние игры",
            description = "Возвращается текущее состояние игры",
            tags = {"Игра"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Текущее состояние доски", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GameResponse.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "В сессии нет id игры", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    })
            }
    )
    @GetMapping("/board")
    public GameResponse getBoard(HttpSession session) {
        log.debug("Запрос доски. gameId = {}",
                session.getAttribute(ttoConfig.getAttrName()));
        return gameMapper.toGameResponse(gameService.getBoard(getGameIdFromSession(session)));
    }

    /**
     * PATCH: Запрос на отмену хода пользователя.
     *
     * @return Ход отменен, текущее состояние доски (status code 200) или Ошибка обработки. Последние два хода по
     * времени принадлежат одному игроку или в сессии нет id игры(status code 400) или Отмена хода недоступна, т.к.
     * пользователь еще не походил или не найдено ходов для данной игры(status code 404)
     */
    @Operation(
            operationId = "cancelStep",
            summary = "Отмена хода пользователя",
            description = "Запрос на отмену хода пользователя, если ходов пользователя нет или игра завершена - ошибка.",
            tags = {"Игра"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ход отменен, текущее состояние доски", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GameResponse.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Ошибка обработки. Последние два хода по времени принадлежат одному игроку или в сессии нет id игры", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Отмена хода недоступна. Возможные причины: пользователь еще не походил, игра завершена", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    })
            }
    )
    @PatchMapping("/cancelStep")
    public GameResponse cancelStep(HttpSession session) {
        log.debug("Запрос на отмену хода. gameId = {}",
                session.getAttribute(ttoConfig.getAttrName()));
        gameService.cancelStep(getGameIdFromSession(session));
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
