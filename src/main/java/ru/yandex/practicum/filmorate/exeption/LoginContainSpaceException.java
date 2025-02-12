package ru.yandex.practicum.filmorate.exeption;

public class LoginContainSpaceException extends RuntimeException {
    public LoginContainSpaceException() {
        super("Логин содержит пробелы");
    }
}
