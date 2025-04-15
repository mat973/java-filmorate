package ru.yandex.practicum.filmorate.exception;

public class NotNegativeIdException extends RuntimeException {
    public NotNegativeIdException(String message) {
        super(message);
    }
}
