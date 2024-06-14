package ru.pikistenev.tictactoe.mainservice.enums;

import lombok.AllArgsConstructor;

/**
 * Победитель или ничья.
 */

@AllArgsConstructor
public enum Winner {
    AI,
    USER,
    DRAW
}
