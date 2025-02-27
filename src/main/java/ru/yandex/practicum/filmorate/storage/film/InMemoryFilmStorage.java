package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public Film save(Film film) {
        film.setId(currentId++);
        film.setLikes(new HashSet<>());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> find(Long id) {
        if (!filmMap.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(filmMap.get(id));
    }

    public List<Film> getAllFilms() {
        return filmMap.values().stream().toList();
    }
}
