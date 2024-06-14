package ru.pikistenev.tictactoe.mainservice.service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.enums.GameLevel;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.exception.ForbiddenException;
import ru.pikistenev.tictactoe.mainservice.exception.NotFoundException;
import ru.pikistenev.tictactoe.mainservice.exception.ValidationException;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;
import ru.pikistenev.tictactoe.mainservice.repository.GameRepository;
import ru.pikistenev.tictactoe.mainservice.repository.StepRepository;
import ru.pikistenev.tictactoe.mainservice.utils.AiStep;
import ru.pikistenev.tictactoe.mainservice.utils.CheckWinner;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final StepRepository stepRepository;
    private final AiStep aiStep;
    private final TtoConfig ttoConfig;
    private final CheckWinner checkWinner;

    @Override
    @Transactional
    public Game startGame(Boolean isStartUser) {
        log.debug("[GameServiceImpl][startGame] Запускаем новую игру. Первый ход пользователя = {}", isStartUser);
        //Создаем игру
        Game game = Game.builder()
                .isUserStart(isStartUser)
                .steps(new ArrayList<>())
                .level(GameLevel.EASY)
                .status(GameStatus.IN_PROGRESS)
                .aiSymbol(ttoConfig.getAiSymbol())
                .userSymbol(ttoConfig.getUserSymbol())
                .build();
        Game createdGame = gameRepository.save(game);

        //Если первым ходит не пользователь - определим ход машины
        if (!isStartUser) {
            Step currentAiStep = Step.builder()
                    .cell(aiStep.findAiStepCell(game))
                    .isUserStep(false)
                    .game(game)
                    .build();
            createdGame.getSteps().add(currentAiStep);
            stepRepository.save(currentAiStep);
            gameRepository.save(createdGame);
        }

        return createdGame;
    }

    @Override
    @Transactional
    public Game userStep(UUID gameId, Integer cell) {
        log.debug("[GameServiceImpl][userStep] Обрабатываем ход пользователя. Id игры = {}, Номер ячейки куда хочет походить пользователь = {}",
                gameId,
                cell);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Игра с данным id не найдена"));

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            throw new ForbiddenException("Игра уже завершена");
        }

        boolean stepExists = game.getSteps().stream().anyMatch(step -> step.getCell().equals(cell));
        if (stepExists) {
            throw new ForbiddenException("Данное поле недоступно");
        }

        //Создаем шаг
        Step currentUserStep = Step.builder()
                .cell(cell)
                .isUserStep(true)
                .game(game)
                .build();

        game.getSteps().add(currentUserStep);
        stepRepository.save(currentUserStep);

        //Проверяем победил ли пользователь этим шагом
        if (checkWinner(game)) {
            gameRepository.save(game);
            return game;
        }

        //Создаем шаг машины
        Step currentAiStep = Step.builder()
                .cell(aiStep.findAiStepCell(game))
                .isUserStep(false)
                .game(game)
                .build();

        game.getSteps().add(currentAiStep);
        stepRepository.save(currentAiStep);
        //Проверяем победила ли машина своим ходом
        checkWinner(game);
        gameRepository.save(game);
        return game;
    }

    @Override
    public Game getBoard(UUID gameId) {
        log.debug("[GameServiceImpl][getBoard] Обрабатываем запрос на получение доски. Id игры = {}", gameId);
        return gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Игра с данным id не найдена"));
    }

    @Override
    @Transactional
    public void cancelStep(UUID gameId) {
        log.debug("[GameServiceImpl][cancelStep] Обрабатываем запрос на отмену хода. Id игры = {}", gameId);
        Optional<List<Step>> stepToCancel = stepRepository.findFirst2ByGameIdAndGame_Status_NotOrderByUpdatedDesc(gameId, GameStatus.FINISHED);

        if (stepToCancel.isEmpty() || stepToCancel.get().size() < 2) {
            throw new NotFoundException("Отмена хода недоступна. Возможные причины: пользователь еще не походил, игра завершена");
        }

        if (stepToCancel.get().get(0).getIsUserStep().equals(stepToCancel.get().get(1).getIsUserStep())) {
            throw new ValidationException("Ошибка обработки. Последние два хода по времени принадлежат одному игроку.");
        }

        stepRepository.deleteByIdIn(stepToCancel.get().stream().map( Step::getId).toList());
    }

    /**
     * Вызваем проверку победителя, при необходимсти завершаем игру.
     *
     * @param game Текущее состояние игры
     * @return true - если победитель определен или ничья. false - если игра продолжается.
     */
    private boolean checkWinner(Game game) {
        checkWinner.check(game);
        if (game.getWinner() != null) {
            game.setStatus(GameStatus.FINISHED);
            return true;
        }
        return false;
    }
}
