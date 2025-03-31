package ru.yandex.practicum.filmorate.exeption;

public class IncorrectOfRatingException extends RuntimeException {
    public IncorrectOfRatingException(String s) {
        super(s);
    }
}
