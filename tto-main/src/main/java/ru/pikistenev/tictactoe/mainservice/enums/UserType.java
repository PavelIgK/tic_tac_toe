package ru.pikistenev.tictactoe.mainservice.enums;

import lombok.AllArgsConstructor;

/**
 * Для определения чья клетка на доске.
 */
@AllArgsConstructor
public enum UserType {
    AI,
    USER,
    FREE
}
