package ru.pikistenev.tictactoe.mainservice.service;


import java.util.HashSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.exception.ForbiddenException;
import ru.pikistenev.tictactoe.mainservice.exception.NotFoundException;
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
        Game game = Game.builder()
                .isUserStart(isStartUser)
                .steps(new HashSet<>())
                .status(GameStatus.IN_PROGRESS)
                .aiSymbol(ttoConfig.getAiSymbol())
                .userSymbol(ttoConfig.getUserSymbol())
                .build();
        Game createdGame = gameRepository.save(game);
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
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Игра с данным id не найдена"));
        if (game.getStatus().equals(GameStatus.FINISHED)) {
            throw new ForbiddenException("Игра уже завершена");
        }

        boolean stepExists = game.getSteps().stream().anyMatch(step -> step.getCell().equals(cell));
        if (stepExists) {
            throw new ForbiddenException("Данное поле недоступно");
        }


        Step currentUserStep = Step.builder()
                .cell(cell)
                .isUserStep(true)
                .game(game)
                .build();

        game.getSteps().add(currentUserStep);
        stepRepository.save(currentUserStep);

        if (checkWinner(game)) {
            gameRepository.save(game);
            return game;
        }

        Step currentAiStep = Step.builder()
                .cell(aiStep.findAiStepCell(game))
                .isUserStep(false)
                .game(game)
                .build();

        game.getSteps().add(currentAiStep);
        stepRepository.save(currentAiStep);
        checkWinner(game);
        gameRepository.save(game);
        return game;
    }

    public Game getBoard(UUID gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new NotFoundException("Игра с данным id не найдена"));
    }

    private boolean checkWinner(Game game) {
        checkWinner.check(game);
        if (game.getWinner() != null) {
            game.setStatus(GameStatus.FINISHED);
            return true;
        }
        return false;
    }
}
