package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;
@Component("film-bd")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Film> mapper;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    private static final String FIND_ALL_QUERY = "SELECT f.*, r.name AS rating, " +
            "STRING_AGG(g.name, ', ') AS genres FROM films f " +
            "JOIN ratings r ON f.rating_id = r.rating_id " +
            "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
            "GROUP BY f.film_id, r.name";

    private static final String FIND_BY_ID_QUERY = "SELECT f.*, r.name AS rating, " +
            "STRING_AGG(g.name, ', ') AS genres FROM films f " +
            "JOIN ratings r ON f.rating_id = r.rating_id " +
            "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
            "WHERE f.film_id = ? " +
            "GROUP BY f.film_id, r.name";

    private static final String INSERT_QUERY = "INSERT INTO films (title, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE films SET title = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ? WHERE film_id = ?";

    private static final String EXIST_BY_ID_QUERY = "SELECT COUNT(*) > 0 FROM films WHERE film_id = ?";

    private static final String GET_POPULAR_FILMS_QUERY = "SELECT f.*, r.name AS rating, " +
            "ARRAY_AGG(g.name) AS genres FROM films f " +
            "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
            "JOIN ratings r ON f.rating_id = r.rating_id " +
            "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
            "GROUP BY f.film_id, r.name " +
            "ORDER BY COUNT(fl.user_id) DESC LIMIT ?";

    @Override
    public Film save(Film film) {
        jdbc.update(INSERT_QUERY, film.getTitle(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRatingId());
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbc.update(UPDATE_QUERY, film.getTitle(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRatingId(), film.getId());
        return film;
    }

    @Override
    public Optional<Film> find(Long id) {
        return Optional.ofNullable(jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id));
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Boolean existById(Long id) {
        return jdbc.queryForObject(EXIST_BY_ID_QUERY, Boolean.class, id);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return jdbc.query(GET_POPULAR_FILMS_QUERY, mapper, count);
    }
}


}
