package ru.practicum.errors.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StateIsNotSupportException extends RuntimeException {
    public StateIsNotSupportException(String message) {
        super(message);
    }
}