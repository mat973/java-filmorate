package ru.yandex.practicum.filmorate.exeption;

public class NotNegativeIdException extends RuntimeException {
    public NotNegativeIdException(String message) {
        super(message);
    }
}
