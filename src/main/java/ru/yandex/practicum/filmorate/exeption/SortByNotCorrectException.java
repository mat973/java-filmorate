package ru.yandex.practicum.filmorate.exeption;

public class SortByNotCorrectException extends RuntimeException {
    public SortByNotCorrectException(String message) {
        super(message);
    }
}
