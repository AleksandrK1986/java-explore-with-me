package ru.practicum.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.errors.exception.*;


import java.time.LocalDateTime;
import java.util.Collections;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({StateIsNotSupportException.class, NotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.emptyList(),
                e.getMessage(), "",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.emptyList(),
                e.getMessage(), "",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.emptyList(),
                e.getMessage(), "",
                HttpStatus.FORBIDDEN,
                LocalDateTime.now());
        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.emptyList(),
                e.getMessage(), "",
                HttpStatus.FORBIDDEN,
                LocalDateTime.now());
        return errorResponse;
    }

    @ExceptionHandler(value = AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDatabaseExceptions(AlreadyExistException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.emptyList(),
                e.getMessage(), "",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
        return errorResponse;
    }
}
