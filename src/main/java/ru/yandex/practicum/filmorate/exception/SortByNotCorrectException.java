package ru.yandex.practicum.filmorate.exception;

public class SortByNotCorrectException extends RuntimeException {
    public SortByNotCorrectException(String message) {
        super(message);
    }
}
