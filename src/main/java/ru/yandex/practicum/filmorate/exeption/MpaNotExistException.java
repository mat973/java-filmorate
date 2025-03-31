package ru.yandex.practicum.filmorate.exeption;

public class MpaNotExistException extends RuntimeException {
    public MpaNotExistException(String s) {
        super(s);
    }
}
