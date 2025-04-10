package ru.yandex.practicum.filmorate.exeption;

public class DirectorMustHaveNameException extends RuntimeException {
    public DirectorMustHaveNameException(String message) {
        super(message);
    }
}
