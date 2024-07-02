package ru.pikistenev.tictactoe.bot.enums;

import lombok.AllArgsConstructor;

/**
 * Возможные этапы игры.
 */

@AllArgsConstructor
public enum GameStep {
    NEW,
    CHOICE_FIRST_PLAYER,
    CHOICE_LEVEL,
    DURING,
    FINISHED

}
