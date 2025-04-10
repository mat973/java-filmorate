package ru.yandex.practicum.filmorate.exeption;

public class DirectorNotExistException extends RuntimeException {
    public DirectorNotExistException(String message) {
        super(message);
    }
}
