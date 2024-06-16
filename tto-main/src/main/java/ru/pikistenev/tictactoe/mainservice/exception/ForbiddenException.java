package ru.pikistenev.tictactoe.mainservice.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);

    }
}
