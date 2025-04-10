package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    Optional<Film> find(Long filmId);

    List<Film> getPopularFilms(Integer count);

    Boolean existById(Long filmId);

    List<Film> getAllFilms();

    List<Film> getFilmsByName(String query);

    List<Film> getFilmsByDirector(String query);

    List<Film> getFilmsByNameAndDirector(String query);

    void addLike(Long filmId, Long userId);

    void dislike(Long filmId, Long userId);
}