package ru.pikistenev.tictactoe.mainservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    GameRepository gameRepository;

    @Mock
    StepRepository stepRepository;

    @Mock
    AiStep aiStep;

    @InjectMocks
    GameServiceImpl gameService;

    @Mock
    TtoConfig ttoConfig;

    private Game gameOne;
    private Game gameTwo;
    private Game gameFinished;
    private Step stepUserOne;
    private Step stepUserTwo;
    private Step stepAiOne;

    @BeforeEach
    public void init() {
        UUID id = UUID.randomUUID();
        gameOne = Game.builder()
                .id(id)
                .level(GameLevel.EASY)
                .build();

        UUID stepId = UUID.randomUUID();
        stepUserOne = Step.builder()
                .id(stepId)
                .cell(0)
                .isUserStep(true)
                .build();

        UUID stepIdTwo = UUID.randomUUID();
        stepUserTwo = Step.builder()
                .id(stepIdTwo)
                .cell(2)
                .isUserStep(true)
                .build();

        UUID stepIdThree = UUID.randomUUID();
        stepAiOne = Step.builder()
                .id(stepIdThree)
                .cell(1)
                .isUserStep(false)
                .build();

        List<Step> steps = new ArrayList<>();
        UUID idGameTwo = UUID.randomUUID();
        gameTwo = Game.builder()
                .id(idGameTwo)
                .steps(steps)
                .build();


        UUID idFinished = UUID.randomUUID();
        gameFinished = Game.builder()
                .id(idFinished)
                .build();


    }

    @Test
    void startCorrectGame_UserStart() {
        when(gameRepository.save(any(Game.class))).thenReturn(gameOne);
        Game createdGame = gameService.startGame(true, GameLevel.EASY);
        verify(gameRepository, times(1)).save(any());
        assertEquals(gameOne, createdGame);
    }

    @Test
    void startCorrectGame_AiStart() {
        when(gameRepository.save(any(Game.class))).thenReturn(gameTwo);
        when(stepRepository.save(any(Step.class))).thenReturn(stepUserOne);
        when(aiStep.findAiStepCell(any(Game.class))).thenReturn(0);
        Game createdGame = gameService.startGame(false, GameLevel.EASY);
        verify(gameRepository, times(2)).save(any());
        verify(stepRepository, times(1)).save(any());
        assertEquals(createdGame.getSteps().get(0).getCell(), 0);
        assertEquals(createdGame.getId(), gameTwo.getId());
    }

    @Test
    void getUserStep_GameNotFound() {
        assertThrows(NotFoundException.class,
                () -> gameService.userStep(UUID.randomUUID(), 0));
    }


    @Test
    void getUserStep_GameFinished() {
        gameOne.setStatus(GameStatus.FINISHED);
        Optional<Game> gameOptional = Optional.of(gameOne);
        when(gameRepository.findById(any())).thenReturn(gameOptional);
        assertThrows(ForbiddenException.class,
                () -> gameService.userStep(UUID.randomUUID(), 0));
    }

    @Test
    void getUserStep_StepNotExist() {
        gameTwo.setStatus(GameStatus.IN_PROGRESS);
        gameTwo.getSteps().add(Step.builder().cell(0).build());
        Optional<Game> gameOptional = Optional.of(gameTwo);
        when(gameRepository.findById(any())).thenReturn(gameOptional);
        assertThrows(ForbiddenException.class,
                () -> gameService.userStep(UUID.randomUUID(), 0));
    }

    @Test
    void getUserStep_Correct() {
        gameTwo.setStatus(GameStatus.IN_PROGRESS);
        gameTwo.getSteps().add(Step.builder().cell(0).build());
        Optional<Game> gameOptional = Optional.of(gameTwo);
        when(gameRepository.findById(any())).thenReturn(gameOptional);
        gameService.userStep(UUID.randomUUID(), 1);
        verify(gameRepository, times(1)).save(any());
        verify(stepRepository, times(2)).save(any());
        assertEquals(0, gameTwo.getSteps().get(0).getCell());
    }

    @Test
    void getBoard_Correct() {
        Optional<Game> gameOptional = Optional.of(gameOne);
        when(gameRepository.findById(any())).thenReturn(gameOptional);
        gameService.getBoard(UUID.randomUUID());
        verify(gameRepository, times(1)).findById(any());
    }

    @Test
    void getCancel_Correct_GameFinished() {
        gameFinished.setStatus(GameStatus.FINISHED);
        gameFinished.setWinner(Winner.AI);
        List<Step> steps = new ArrayList<>();
        steps.add(stepUserOne);
        steps.add(stepAiOne);
        gameFinished.setSteps(steps);

        Optional<Game> gameOptional = Optional.of(gameFinished);
        assertEquals(2, gameOptional.get().getSteps().size());
        assertEquals(GameStatus.FINISHED, gameOptional.get().getStatus());
        assertEquals(Winner.AI, gameOptional.get().getWinner());

        when(gameRepository.findById(any())).thenReturn(gameOptional);
        gameService.cancelStep(gameFinished.getId());

        assertEquals(0, gameOptional.get().getSteps().size());
        assertEquals(GameStatus.IN_PROGRESS, gameOptional.get().getStatus());
        assertNull(gameOptional.get().getWinner());
    }


    @Test
    void getCancel_Correct_LastStepUser() {
        List<Step> steps = new ArrayList<>();
        steps.add(stepUserOne);
        steps.add(stepAiOne);
        steps.add(stepUserTwo);
        gameFinished.setSteps(steps);

        Optional<Game> gameOptional = Optional.of(gameFinished);
        assertEquals(3, gameOptional.get().getSteps().size());
        assertEquals(stepUserOne, gameOptional.get().getSteps().get(0));
        assertEquals(stepAiOne, gameOptional.get().getSteps().get(1));
        assertEquals(stepUserTwo, gameOptional.get().getSteps().get(2));

        when(gameRepository.findById(any())).thenReturn(gameOptional);
        gameService.cancelStep(gameFinished.getId());
        assertEquals(2, gameOptional.get().getSteps().size());
        assertEquals(stepUserOne, gameOptional.get().getSteps().get(0));
        assertEquals(stepAiOne, gameOptional.get().getSteps().get(1));
    }


    @Test
    void getCancel_StepOnePlayer() {
        gameFinished.setStatus(GameStatus.FINISHED);
        gameFinished.setWinner(Winner.AI);
        List<Step> steps = new ArrayList<>();
        steps.add(stepUserOne);
        steps.add(stepUserTwo);
        gameFinished.setSteps(steps);

        Optional<Game> gameOptional = Optional.of(gameFinished);

        assertEquals(2, gameOptional.get().getSteps().size());
        when(gameRepository.findById(any())).thenReturn(gameOptional);

        assertThrows(ValidationException.class,
                () -> gameService.cancelStep(gameOne.getId()));
        assertEquals(2, gameOptional.get().getSteps().size());
    }

    @Test
    void getCancel_No_Steps() {
        Optional<Game> gameOptional = Optional.of(gameOne);
        assertNull(gameOptional.get().getSteps());

        when(gameRepository.findById(any())).thenReturn(gameOptional);
        assertThrows(ValidationException.class,
                () -> gameService.cancelStep(gameOne.getId()));
    }


    @Test
    void getCancel_Correct() {
        gameOne.setStatus(GameStatus.IN_PROGRESS);
        List<Step> steps = new ArrayList<>();
        steps.add(stepUserOne);
        steps.add(stepAiOne);
        gameOne.setSteps(steps);

        Optional<Game> gameOptional = Optional.of(gameOne);
        assertEquals(2, gameOptional.get().getSteps().size());
        assertEquals(GameStatus.IN_PROGRESS, gameOptional.get().getStatus());
        assertNull(gameOptional.get().getWinner());

        when(gameRepository.findById(any())).thenReturn(gameOptional);
        gameService.cancelStep(gameOne.getId());

        assertEquals(0, gameOptional.get().getSteps().size());
        assertEquals(GameStatus.IN_PROGRESS, gameOptional.get().getStatus());
        assertNull(gameOptional.get().getWinner());
    }


}
