package ru.pikistenev.tictactoe.mainservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ДТО для возвращения информации об игре.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@Schema(name = "GameResponse", description = "Состояние доски")
public class GameResponse {

    @Schema(name = "id", description = "id игры", example = "1d5b56be-1835-4623-b037-e0acb0a66b08")
    private UUID id;

    @Schema(name = "board", description = "состояние доски", example = "[\"X\",\"0\",\"0\",\"X\",\"\",\"\",\"\",\"\",\"\"]")
    private List<String> board;

    @Schema(name = "status", description = "Статус игры", example = "FINISHED")
    private String status;

    @Schema(name = "winner", description = "Победитель", example = "AI")
    private String winner;

}
