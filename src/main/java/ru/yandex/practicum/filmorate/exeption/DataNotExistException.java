package ru.yandex.practicum.filmorate.exeption;

public class DataNotExistException extends RuntimeException {
    public DataNotExistException(String message) {
        super(message);
    }
}
