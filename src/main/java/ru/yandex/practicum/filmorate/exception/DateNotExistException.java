package ru.yandex.practicum.filmorate.exception;

public class DateNotExistException extends RuntimeException {
    public DateNotExistException(String message) {
        super(message);
    }
}
