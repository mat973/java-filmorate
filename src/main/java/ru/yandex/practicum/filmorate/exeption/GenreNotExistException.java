package ru.yandex.practicum.filmorate.exeption;

public class GenreNotExistException extends RuntimeException {
    public GenreNotExistException(String s) {
        super(s);
    }
}
