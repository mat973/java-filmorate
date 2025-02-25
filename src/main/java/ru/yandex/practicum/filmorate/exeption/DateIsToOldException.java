package ru.yandex.practicum.filmorate.exeption;

public class DateIsToOldException extends RuntimeException {
    public DateIsToOldException(String message) {
        super(message);
    }
}
