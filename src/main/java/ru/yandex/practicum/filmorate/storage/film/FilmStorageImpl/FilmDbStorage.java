package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("film-bd")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
    }

    private static final String FIND_ALL_QUERY =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN ratings r ON f.rating_id = r.rating_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN ratings r ON f.rating_id = r.rating_id " +
                    "WHERE f.film_id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO films (title, description, release_date, duration, rating_id) " +
                    "VALUES (?, ?, ?, ?, (SELECT rating_id FROM ratings WHERE name = ?))";

    private static final String UPDATE_QUERY =
            "UPDATE films SET title = ?, description = ?, release_date = ?, " +
                    "duration = ?, rating_id = (SELECT rating_id FROM ratings WHERE name = ?) " +
                    "WHERE film_id = ?";

    private static final String GET_FILM_GENRES_QUERY =
            "SELECT g.name " +
                    "FROM film_genre fg " +
                    "JOIN genre g ON fg.genre_id = g.genre_id " +
                    "WHERE fg.film_id = ?";

    private static final String GET_POPULAR_FILMS_QUERY =
            "SELECT f.*, r.name AS mpa_name FROM films f " +
                    "LEFT JOIN ratings r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                    "GROUP BY f.film_id, r.name " +
                    "ORDER BY COUNT(fl.user_id) DESC LIMIT ?";
    private static final String EXIST_BY_ID_QUERY = "SELECT COUNT(*) > 0 FROM films WHERE film_id = ?";

    @Override
    public Film save(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getTitle());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            ps.setString(5, film.getMpa().getTitle());
            return ps;
        }, keyHolder);

        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        saveFilmGenres(filmId, film.getGenres());
        return film;
    }

    private void saveFilmGenres(long filmId, List<String> genres) {
        if (genres == null || genres.isEmpty()) return;

        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", filmId);

        String sql = "INSERT INTO film_genre (film_id, genre_id) " +
                "SELECT ?, genre_id FROM genre WHERE name = ?";

        List<Object[]> batchArgs = genres.stream()
                .map(genreName -> new Object[]{filmId, genreName})
                .collect(Collectors.toList());

        jdbc.batchUpdate(sql, batchArgs);
    }

    @Override
    public Film update(Film film) {
        jdbc.update(UPDATE_QUERY,
                film.getTitle(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa(),
                film.getId());

        saveFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Optional<Film> find(Long id) {
        try {
            Film film = jdbc.queryForObject(FIND_BY_ID_QUERY, filmRowMapper, id);
            if (film != null) {
                List<String> genres = jdbc.queryForList(
                        GET_FILM_GENRES_QUERY, String.class, id);
                film.setGenres(genres);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbc.query(FIND_ALL_QUERY, filmRowMapper);

        if (!films.isEmpty()) {
            Map<Long, Film> filmMap = films.stream()
                    .collect(Collectors.toMap(Film::getId, Function.identity()));

            String allGenresSql = "SELECT fg.film_id, g.name " +
                    "FROM film_genre fg " +
                    "JOIN genre g ON fg.genre_id = g.genre_id " +
                    "WHERE fg.film_id IN (" +
                    films.stream().map(f -> f.getId().toString())
                            .collect(Collectors.joining(",")) + ")";

            jdbc.query(allGenresSql, rs -> {
                Film film = filmMap.get(rs.getLong("film_id"));
                if (film != null) {
                    film.getGenres().add(rs.getString("name"));
                }
            });
        }

        return films;
    }

    @Override
    public Boolean existById(Long id) {
        return jdbc.queryForObject(EXIST_BY_ID_QUERY, Boolean.class, id);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return jdbc.query(GET_POPULAR_FILMS_QUERY, filmRowMapper, count);
    }
}