package ru.pikistenev.tictactoe.mainservice.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class GameResponseTest {

    @Autowired
    private JacksonTester<GameResponse> json;

    @Test
    void testSerialization() throws IOException {
        GameResponse gameResponse = new GameResponse();
        UUID uuid = UUID.randomUUID();
        gameResponse.setId(uuid);
        gameResponse.setWinner("AI");
        List<String> board = new ArrayList<>();
        board.add("X");
        board.add("O");
        gameResponse.setBoard(board);
        gameResponse.setStatus("FINISHED");

        JsonContent<GameResponse> result = json.write(gameResponse);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.board");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).hasJsonPath("$.winner");

    }
}