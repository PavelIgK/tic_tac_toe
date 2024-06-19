package ru.pikistenev.tictactoe.mainservice.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.pikistenev.tictactoe.mainservice.enums.GameLevel;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;

@SpringBootTest
@AutoConfigureMockMvc
@MockBean(RemoveGame.class)
class AiStepTest {

    @InjectMocks
    AiStep aiStep;

    Game gameOne;

    @BeforeEach
    public void init() {
        UUID id = UUID.randomUUID();
        gameOne = Game.builder()
                .id(id)
                .level(GameLevel.EASY)
                .steps(new ArrayList<>())
                .build();

    }

    @SneakyThrows
    @Test
    void createCorrectGame_allCells() {
        int result = aiStep.findAiStepCell(gameOne);
        boolean checkResult = result >= 0 && result < 9;
        assertTrue(checkResult);
    }

    @SneakyThrows
    @Test
    void createCorrectGame_cell_5to8() {

        gameOne.setSteps(
                List.of(Step.builder().cell(0).build(),
                        Step.builder().cell(1).build(),
                        Step.builder().cell(2).build(),
                        Step.builder().cell(3).build(),
                        Step.builder().cell(4).build()));
        int result = aiStep.findAiStepCell(gameOne);
        boolean checkResult = result > 4 && result < 9;
        assertTrue(checkResult);
    }
}
