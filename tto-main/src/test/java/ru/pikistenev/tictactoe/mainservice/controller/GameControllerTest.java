package ru.pikistenev.tictactoe.mainservice.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.pikistenev.tictactoe.mainservice.dto.GameResponse;
import ru.pikistenev.tictactoe.mainservice.mapper.GameMapper;
import ru.pikistenev.tictactoe.mainservice.model.Game;
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
    private GameResponse gameResponse;

    @BeforeEach
    public void init() {
        game = new Game();
        game.setId(UUID.randomUUID());
    }

    @SneakyThrows
    @Test
    void createCorrectGame() {
        when(gameService.startGame(any())).thenReturn(game);
        mockMvc.perform(post("/test/")
                        .characterEncoding("UTF_8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(request().sessionAttribute("gameId", game.getId()));

        verify(gameService, times(1)).startGame(any());
    }


}