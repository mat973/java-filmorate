package ru.yandex.practicum.filmorate.exception;

public class DirectorMustHaveNameException extends RuntimeException {
    public DirectorMustHaveNameException(String message) {
        super(message);
    }
}
