package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    Optional<Film> find(Long id);

    List<Film> getPopularFilms(Integer count);

    Boolean existById(Long id);

    List<Film> getAllFilms();

    void addLike(Long filmId, Long userId);

    void dislike(Long filmId, Long userId);


}
