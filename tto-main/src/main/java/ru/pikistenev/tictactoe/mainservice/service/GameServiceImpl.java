package ru.pikistenev.tictactoe.mainservice.service;


import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.enums.GameLevel;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;
import ru.pikistenev.tictactoe.mainservice.exception.ForbiddenException;
import ru.pikistenev.tictactoe.mainservice.exception.NotFoundException;
import ru.pikistenev.tictactoe.mainservice.exception.ValidationException;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;
import ru.pikistenev.tictactoe.mainservice.repository.GameRepository;
import ru.pikistenev.tictactoe.mainservice.repository.StepRepository;
import ru.pikistenev.tictactoe.mainservice.utils.AiStep;
import ru.pikistenev.tictactoe.mainservice.utils.Board;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final StepRepository stepRepository;
    private final AiStep aiStep;
    private final TtoConfig ttoConfig;

    @Override
    @Transactional
    public Game startGame(boolean isStartUser, GameLevel gameLevel) {
        log.debug("Запускаем новую игру. Первый ход пользователя = {}, уровень игры = {}", isStartUser, gameLevel);
        //Создаем игру
        Game game = Game.builder()
                .isUserStart(isStartUser)
                .steps(new ArrayList<>())
                .level(gameLevel)
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
    public Game userStep(UUID gameId, int cell) {
        log.debug("Обрабатываем ход пользователя. Id игры = {}, Номер ячейки куда хочет походить пользователь = {}",
                gameId,
                cell);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Игра с данным id не найдена"));

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            throw new ForbiddenException("Игра уже завершена");
        }

        boolean stepExists = game.getSteps().stream().anyMatch(step -> step.getCell() == cell);
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
        log.debug("Обрабатываем запрос на получение доски. Id игры = {}", gameId);
        return gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Игра с данным id не найдена"));
    }

    @Override
    @Transactional
    public void cancelStep(UUID gameId) {
        log.debug("Обрабатываем запрос на отмену хода. Id игры = {}", gameId);

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Игра с данным id не найдена"));


        if (game.getSteps() == null || game.getSteps().size() < 2) {
            throw new ValidationException("Отмена хода недоступна: пользователь еще не походил.");
        }

        int sizeSteps = game.getSteps().size();
        if (game.getSteps().get(sizeSteps - 1).isUserStep() == game.getSteps().get(sizeSteps - 2).isUserStep()) {
            throw new ValidationException("Ошибка обработки. Последние два хода по времени принадлежат одному игроку.");
        }

        //Если последний ход пользователя(случай когда игра завершается ходом пользователя) - удаляем только его.
        if (game.getSteps().get(sizeSteps - 1).isUserStep()) {
            game.getSteps().remove(sizeSteps - 1);
        } else {
            game.getSteps().remove(sizeSteps - 1);
            game.getSteps().remove(sizeSteps - 2);
        }

        //Если игра была завершена, вернем ее статус обратно и уберем победителя.
        if (game.getStatus() != null && game.getStatus().equals(GameStatus.FINISHED)) {
            game.setStatus(GameStatus.IN_PROGRESS);
            game.setWinner(null);
        }
        gameRepository.save(game);
    }

    /**
     * Вызваем проверку победителя, при необходимсти завершаем игру.
     *
     * @param game Текущее состояние игры
     * @return true - если победитель определен или ничья. false - если игра продолжается.
     */
    private boolean checkWinner(Game game) {
        Board board = new Board(game);
        Winner winner = board.checkWinner();
        if (winner != null) {
            game.setWinner(winner);
            game.setStatus(GameStatus.FINISHED);
            return true;
        }
        return false;
    }
}
