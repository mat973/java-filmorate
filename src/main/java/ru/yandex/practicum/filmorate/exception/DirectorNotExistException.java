package ru.yandex.practicum.filmorate.exception;

public class DirectorNotExistException extends RuntimeException {
    public DirectorNotExistException(String message) {
        super(message);
    }
}
