package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    Optional<Film> find(Long id);
}
