package ru.yandex.practicum.filmorate.exeption;

public class DateNotExistException extends RuntimeException {
    public DateNotExistException(String message) {
        super(message);
    }
}
