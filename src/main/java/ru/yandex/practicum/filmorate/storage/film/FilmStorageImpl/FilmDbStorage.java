package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.Genre;
import ru.yandex.practicum.filmorate.exeption.FilmNotUpdateException;
import ru.yandex.practicum.filmorate.exeption.GenreNotExistException;
import ru.yandex.practicum.filmorate.exeption.MpaNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component("film-bd")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
    }


    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM films f WHERE f.film_id = ?";
    private static final String FIND_BY_ID_GENRE_QUERY =
            "SELECT genre_id FROM  film_genre where film_id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO films (title, description, release_date, duration, rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_FILM_GENRE_QUERY =
            "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM_GENRE_QUERY =
            "DELETE FROM film_genre WHERE film_id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT f.film_id, f.title, f.description, f.release_date, f.duration, f.RATING_ID AS rating_id,\n" +
            "       STRING_AGG(fg.genre_id, ',') AS genres\n" +
            "FROM films f\n" +
            "JOIN ratings r ON f.rating_id = r.rating_id\n" +
            "JOIN film_genre fg ON f.film_id = fg.film_id\n" +
            "GROUP BY f.film_id\n" +
            "ORDER BY f.film_id";
    private static final String GET_FILM_BY_ID = "SELECT f.film_id, f.title, f.description, f.release_date, f.duration, f.RATING_ID AS rating_id,\n" +
            "       STRING_AGG(fg.genre_id, ',') AS genres\n" +
            "FROM films f\n" +
            "JOIN ratings r ON f.rating_id = r.rating_id\n" +
            "JOIN film_genre fg ON f.film_id = fg.film_id\n" +
            "WHERE f.FILM_ID = ?\n" +
            "GROUP BY f.film_id";

    private static final String SET_LIKE_QUERY = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES (?,?)";

    private static final String SET_DISLIKE_QUERY = "DELETE FROM FILM_LIKES WHERE user_id = ? AND FILM_ID = ?";
        private static final String GET_POPULAR_FILMS_QUERY =
            "SELECT \n" +
                    "    f.film_id, \n" +
                    "    f.title, \n" +
                    "    f.description, \n" +
                    "    f.release_date, \n" +
                    "    f.duration, \n" +
                    "    f.rating_id,\n" +
                    "    STRING_AGG(fg.genre_id, ',') AS genres\n" +
                    "FROM films f\n" +
                    "JOIN ratings r ON f.rating_id = r.rating_id\n" +
                    "JOIN film_genre fg ON f.film_id = fg.film_id\n" +
                    "LEFT JOIN film_likes fl ON fl.film_id = f.film_id\n" +
                    "GROUP BY f.film_id\n" +
                    "ORDER BY \n" +
                    "    COUNT(fl.user_id) DESC, \n" +
                    "    f.film_id ASC\n" +
                    "LIMIT ?";
    private static final String EXIST_BY_ID_QUERY = "SELECT COUNT(*) > 0 FROM films WHERE film_id = ?";
    private static final String EXIST_MPA_BY_ID_QUERY = "SELECT COUNT(*) FROM ratings WHERE rating_id = ?";
    private static final String EXIST_GENRE_BY_ID_QUERY = "SELECT COUNT(*)  FROM genre where genre_id = ?";
    private static final String GET_FILM_BY_ID_GENRE = """
        SELECT 
            f.film_id AS id,
            f.title AS name,
            f.description,
            f.release_date AS releaseDate,
            f.duration,
            r.rating_id AS "mpa.id",
            r.name AS "mpa.name",
            JSON_AGG(
                JSON_BUILD_OBJECT(
                    'id', g.genre_id,
                    'name', g.name
                )
            ) AS genres
        FROM films f
        JOIN ratings r ON f.rating_id = r.rating_id
        LEFT JOIN film_genre fg ON f.film_id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.genre_id
        WHERE f.film_id = ?
        GROUP BY f.film_id, r.rating_id
        """;

    @Override
    public Film save(Film film) {
        if (jdbc.queryForObject(EXIST_MPA_BY_ID_QUERY, Integer.class, film.getMpa()) == 0) {
            throw new MpaNotExistException("Рейтинга с id = " + film.getMpa() + " не существует");
        }
        if (!film.getGenres().stream()
                .allMatch(genre -> jdbc.queryForObject(EXIST_GENRE_BY_ID_QUERY, Integer.class, genre) > 0)) {
            throw new GenreNotExistException("Жанра который вы указали не существует");
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getTitle());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            ps.setInt(5, film.getMpa());
            return ps;
        }, keyHolder);

        long filmId = keyHolder.getKey().longValue();
        film.getGenres().forEach(x -> jdbc.update(INSERT_FILM_GENRE_QUERY, filmId, x));
        film.setId(filmId);

        return film;
    }

    @Override
    public Film update(Film film) {
        int updated = jdbc.update(UPDATE_QUERY,
                film.getTitle(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa(),
                film.getId());

        if (updated == 0) {
            throw new FilmNotUpdateException("Фильм не обновился");
        }

        jdbc.update(DELETE_FILM_GENRE_QUERY, film.getId());

        film.getGenres().forEach(genre ->
                jdbc.update(INSERT_FILM_GENRE_QUERY, film.getId(), genre));

        return film;
    }

    @Override
    public Optional<Film> find(Long id) {
        try {
            Film film = jdbc.queryForObject(GET_FILM_BY_ID, filmRowMapper, id);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbc.query(GET_ALL_FILMS_QUERY, filmRowMapper);
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(SET_LIKE_QUERY, userId, filmId);
    }

    @Override
    public void dislike(Long filmId, Long userId) {
        jdbc.update(SET_DISLIKE_QUERY, userId, filmId);
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