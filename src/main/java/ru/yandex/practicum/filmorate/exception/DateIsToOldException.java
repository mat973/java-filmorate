package ru.yandex.practicum.filmorate.exception;

public class DateIsToOldException extends RuntimeException {
    public DateIsToOldException(String message) {
        super(message);
    }
}
