package ru.pikistenev.tictactoe.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Обработчик ошибок.
 */

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ApiError handleValidationExceptionException(ValidationException exception) {
        String message = exception.getMessage();
        log.warn("[ApiExceptionHandler][handleValidationExceptionException] {}", message);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .reason("Запрос некорректен.")
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ApiError handleForbiddenExceptionException(ForbiddenException exception) {
        String message = exception.getMessage();
        log.warn("[ApiExceptionHandler][handleForbiddenExceptionException] {}", message);
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(message)
                .reason("Запрещено.")
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundException.class)
    public ApiError handleNotFoundException(NotFoundException exception) {
        String message = exception.getMessage();
        log.warn("[ApiExceptionHandler][handleNotFoundException] {}", message);

        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(message)
                .reason("Игра не найдена.")
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getMessage();
        log.warn("[ApiExceptionHandler][handleMethodArgumentNotValidException] {}", message);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .reason("Запрос некорректен.")
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable throwable) {
        log.error("Внутренняя ошибка сервера. {}", throwable.getMessage());
        String message = throwable.getMessage();
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .reason("Произошла непредвиденная ошибка.")
                .build();
    }

}
