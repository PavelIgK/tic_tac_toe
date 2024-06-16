package ru.pikistenev.tictactoe.mainservice.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.pikistenev.tictactoe.mainservice.enums.UserType;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.model.Step;

@SpringBootTest
@MockBean(RemoveGame.class)
class BoardTest {

    Board board;

    Game gameOne;

    @BeforeEach
    public void init() {

        UUID id = UUID.randomUUID();
        gameOne = Game.builder()
                .id(id)
                .steps(new ArrayList<>())
                .build();

    }

    @SneakyThrows
    @Test
    void checkFreeBoard() {
        board = new Board(gameOne);
        List<Integer> freeCells = board.freeCells();
        assertTrue(freeCells
                .containsAll(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)));
    }

    @SneakyThrows
    @Test
    void checkFreeBoard_0to4Busy() {
        gameOne.setAiSymbol("X");
        gameOne.setUserSymbol("O");
        gameOne.setSteps(
                List.of(Step.builder().cell(0).build(),
                        Step.builder().cell(1).build(),
                        Step.builder().cell(2).build(),
                        Step.builder().cell(3).build(),
                        Step.builder().cell(4).build()));
        board = new Board(gameOne);
        List<Integer> freeCells = board.freeCells();
        assertTrue(freeCells
                .containsAll(List.of(5, 6, 7, 8)));
        assertFalse(freeCells.contains(0));
        assertFalse(freeCells.contains(1));
        assertFalse(freeCells.contains(2));
        assertFalse(freeCells.contains(3));
        assertFalse(freeCells.contains(4));
    }


    @SneakyThrows
    @Test
    void checkBoard() {
        gameOne.setSteps(
                List.of(Step.builder().cell(0).isUserStep(true).build(),
                        Step.builder().cell(1).isUserStep(true).build(),
                        Step.builder().cell(2).isUserStep(false).build(),
                        Step.builder().cell(3).isUserStep(true).build(),
                        Step.builder().cell(4).isUserStep(false).build()));
        board = new Board(gameOne);

        assertEquals(board.getBoard().get(0), UserType.USER);
        assertEquals(board.getBoard().get(1), UserType.USER);
        assertEquals(board.getBoard().get(2), UserType.AI);
        assertEquals(board.getBoard().get(3), UserType.USER);
        assertEquals(board.getBoard().get(4), UserType.AI);
        assertEquals(board.getBoard().get(5), UserType.FREE);
        assertEquals(board.getBoard().get(6), UserType.FREE);
        assertEquals(board.getBoard().get(7), UserType.FREE);
        assertEquals(board.getBoard().get(8), UserType.FREE);

    }

    @ParameterizedTest
    @MethodSource("generateDataForCheckWin")
    void checkWin(List<UserType> playerInCell, Winner winnerExists) {
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < playerInCell.size(); i++) {
            if (UserType.AI.equals(playerInCell.get(i))) {
                steps.add(Step.builder().cell(i).isUserStep(false).build());
            } else if (UserType.USER.equals(playerInCell.get(i))) {
                steps.add(Step.builder().cell(i).isUserStep(true).build());
            }
        }
        gameOne.setSteps(steps);
        board = new Board(gameOne);
        Winner winner = board.checkWinner();
        assertEquals(winnerExists, winner);
    }

    static Stream<Arguments> generateDataForCheckWin() {
        UserType user = UserType.USER;
        UserType ai = UserType.AI;
        UserType free = UserType.FREE;
        Winner winUser = Winner.USER;
        Winner winAi = Winner.AI;
        Winner draw = Winner.DRAW;
        return Stream.of(
                //Проверяем когда мало ходов
                Arguments.of(List.of(user), null),
                Arguments.of(Arrays.asList(user, ai), null),
                Arguments.of(Arrays.asList(user, ai, user), null),
                Arguments.of(Arrays.asList(user, ai, user, ai), null),
                Arguments.of(Arrays.asList(user, ai, user, ai, user), null),
                Arguments.of(Arrays.asList(user, ai, user, ai, user), null),
                Arguments.of(Arrays.asList(user, ai, user, ai, user, ai), null),
                //Диагонали
                Arguments.of(Arrays.asList(user, ai, user, ai, user, ai, user, free, free), winUser),
                Arguments.of(Arrays.asList(user, ai, user, ai, user, ai, user, ai, free), winUser),
                Arguments.of(Arrays.asList(user, ai, user, ai, user, ai, free, ai, user), winUser),
                Arguments.of(Arrays.asList(user, ai, user, ai, user, ai, ai, ai, user), winUser),
                //линии
                Arguments.of(Arrays.asList(user, user, user, user, ai, user, free, free, free), winUser),
                Arguments.of(Arrays.asList(ai, user, ai, user, user, user, free, free, free), winUser),
                Arguments.of(Arrays.asList(ai, user, ai, user, ai, user, user, user, user), winUser),
                //столбцы
                Arguments.of(Arrays.asList(ai, user, ai, ai, user, user, ai, free, free), winAi),
                Arguments.of(Arrays.asList(user, ai, ai, ai, ai, user, free, ai, free), winAi),
                Arguments.of(Arrays.asList(user, user, ai, user, ai, ai, free, free, ai), winAi),
                //Проверим несколько ничьих
                Arguments.of(Arrays.asList(user, ai, user, ai, user, ai, ai, user, ai), draw),
                Arguments.of(Arrays.asList(user, ai, ai, ai, ai, user, user, user, ai), draw),
                Arguments.of(Arrays.asList(user, ai, ai, ai, user, user, user, ai, ai), draw)

        );
    }

}