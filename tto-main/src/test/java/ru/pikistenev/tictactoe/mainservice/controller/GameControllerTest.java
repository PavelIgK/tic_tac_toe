package ru.pikistenev.tictactoe.mainservice.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.pikistenev.tictactoe.mainservice.mapper.GameMapper;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;
import ru.pikistenev.tictactoe.mainservice.service.GameService;
import ru.pikistenev.tictactoe.mainservice.utils.RemoveGame;

@SpringBootTest(properties = {"tto.api.endpoint=test"})
@AutoConfigureMockMvc
@MockBean(RemoveGame.class)
class GameControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GameService gameService;

    @MockBean
    GameMapper gameMapper;

    private Game game;

    @BeforeEach
    public void init() {
        Step stepUser = Step.builder()
                .cell(0)
                .isUserStep(true)
                .build();
        Step stepAi = Step.builder()
                .cell(1)
                .isUserStep(false)
                .build();


        game = Game.builder()
                .id(UUID.randomUUID())
                .steps(new ArrayList<>())
                .build();
        game.getSteps().add(stepUser);
        game.getSteps().add(stepAi);
    }

    @SneakyThrows
    @Test
    void createCorrectGame() {
        when(gameService.startGame(anyBoolean(), any())).thenReturn(game);
        mockMvc.perform(post("/test/")
                        .characterEncoding("UTF_8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(request().sessionAttribute("gameId", game.getId()));

        verify(gameService, times(1)).startGame(anyBoolean(), any());
    }

    @SneakyThrows
    @Test
    void stepUserCorrect_cell0() {
        mockMvc.perform(patch("/test/0")
                        .characterEncoding("UTF_8")
                        .sessionAttr("gameId", game.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(request().sessionAttribute("gameId", game.getId()));

        verify(gameService, times(1)).userStep(game.getId(), 0);
    }

    @SneakyThrows
    @Test
    void stepUserCorrect_cell8() {
        mockMvc.perform(patch("/test/8")
                        .characterEncoding("UTF_8")
                        .sessionAttr("gameId", game.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(request().sessionAttribute("gameId", game.getId()));

        verify(gameService, times(1)).userStep(game.getId(), 8);
    }

    @SneakyThrows
    @Test
    void stepUserInCorrect_cellNegative() {
        mockMvc.perform(patch("/test/-1")
                        .characterEncoding("UTF_8")
                        .sessionAttr("gameId", game.getId()))
                .andExpect(status().is5xxServerError());

        verify(gameService, times(0)).userStep(any(),anyInt());
    }

    @SneakyThrows
    @Test
    void stepUserInCorrect_cell9() {
        mockMvc.perform(patch("/test/9")
                        .characterEncoding("UTF_8")
                        .sessionAttr("gameId", game.getId()))
                .andExpect(status().is5xxServerError());

        verify(gameService, times(0)).userStep(any(),anyInt());
    }


    @SneakyThrows
    @Test
    void getCorrectBoard() {
        when(gameService.getBoard(any())).thenReturn(game);
        mockMvc.perform(get("/test/board")
                        .characterEncoding("UTF_8")
                        .sessionAttr("gameId", game.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(request().sessionAttribute("gameId", game.getId()))
                .andReturn();

        verify(gameService, times(1)).getBoard(any());
    }

    @SneakyThrows
    @Test
    void cancelStep() {
        Mockito.doNothing().when(gameService).cancelStep(any());
        mockMvc.perform(patch("/test/cancelStep")
                        .characterEncoding("UTF_8")
                        .sessionAttr("gameId", game.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(request().sessionAttribute("gameId", game.getId()))
                .andReturn();

        verify(gameService, times(1)).cancelStep(any());
        verify(gameService, times(1)).getBoard(any());
    }

}