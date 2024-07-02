package ru.pikistenev.tictactoe.mainservice.enums;

import lombok.AllArgsConstructor;

/**
 * Победитель или ничья.
 */

@AllArgsConstructor
public enum Winner {
    AI ("Победил компьютер."),
    USER ("Победил пользователь."),
    DRAW ("Ничья.");

    private String value;

    public String getValue() {
        return value;
    }
}
