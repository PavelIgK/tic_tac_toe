package ru.pikistenev.tictactoe.mainservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Сущность для хранения информации о шаге.
 */



@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Move {

    private Integer score;
    private Integer cell;

}
