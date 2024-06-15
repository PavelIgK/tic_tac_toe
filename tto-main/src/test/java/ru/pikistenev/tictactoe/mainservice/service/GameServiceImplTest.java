package ru.pikistenev.tictactoe.mainservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
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
    private Step stepOne;
    private Step stepTwo;
    private Step stepThree;

    @BeforeEach
    public void init() {
        UUID id = UUID.randomUUID();
        gameOne = Game.builder()
                .id(id)
                .build();

        UUID stepId = UUID.randomUUID();
        stepOne = Step.builder()
                .id(stepId)
                .cell(0)
                .isUserStep(true)
                .build();

        UUID stepIdTwo = UUID.randomUUID();
        stepTwo = Step.builder()
                .id(stepIdTwo)
                .cell(0)
                .isUserStep(true)
                .build();

        UUID stepIdThree = UUID.randomUUID();
        stepThree = Step.builder()
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


    }

    @Test
    void startCorrectGame_UserStart() {
        when(gameRepository.save(any(Game.class))).thenReturn(gameOne);
        Game createdGame = gameService.startGame(true);
        verify(gameRepository, times(1)).save(any());
        assertEquals(gameOne, createdGame);
    }

    @Test
    void startCorrectGame_AiStart() {
        when(gameRepository.save(any(Game.class))).thenReturn(gameTwo);
        when(stepRepository.save(any(Step.class))).thenReturn(stepOne);
        when(aiStep.findAiStepCell(any(Game.class))).thenReturn(0);
        Game createdGame = gameService.startGame(false);
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
    void getCancel_GameFinished() {
        Optional<List<Step>> stepsOptional = Optional.of(List.of(stepOne));
        when(stepRepository.findFirst2ByGameIdAndGame_Status_NotOrderByUpdatedDesc(gameOne.getId(),
                GameStatus.FINISHED)).thenReturn(stepsOptional);
        assertThrows(NotFoundException.class,
                () -> gameService.cancelStep(gameOne.getId()));
    }


    @Test
    void getCancel_StepOnePlayer() {
        Optional<List<Step>> stepsOptional = Optional.of(List.of(stepOne, stepTwo));
        when(stepRepository.findFirst2ByGameIdAndGame_Status_NotOrderByUpdatedDesc(gameOne.getId(),
                GameStatus.FINISHED)).thenReturn(stepsOptional);
        assertThrows(ValidationException.class,
                () -> gameService.cancelStep(gameOne.getId()));
    }


    @Test
    void getCancel_Correct() {
        Optional<List<Step>> stepsOptional = Optional.of(List.of(stepOne, stepThree));
        when(stepRepository.findFirst2ByGameIdAndGame_Status_NotOrderByUpdatedDesc(gameOne.getId(),
                GameStatus.FINISHED)).thenReturn(stepsOptional);
        Mockito.doNothing().when(stepRepository).deleteByIdIn(anyList());
        gameService.cancelStep(gameOne.getId());
        verify(stepRepository, times(1)).deleteByIdIn(anyList());
    }


}
