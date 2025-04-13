package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotUpdateException;
import ru.yandex.practicum.filmorate.exception.GenreNotExistException;
import ru.yandex.practicum.filmorate.exception.MpaNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("film-bd")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM films f WHERE f.film_id = ?";
    private static final String FIND_BY_ID_GENRE_QUERY =
            "SELECT genre_id FROM  film_genre where film_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO films (title, description, release_date, duration, rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE_QUERY =
            "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_FILM_DIRECTOR =
            "INSERT INTO director_film (film_id, director_id) VALUES (?, ?)";


    private static final String UPDATE_QUERY =
            "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM_GENRE_QUERY =
            "DELETE FROM film_genre WHERE film_id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM director_film where film_id = ?";
    private static final String GET_ALL_FILMS_QUERY = """
             SELECT f.film_id, f.title, f.description, f.release_date, f.duration, f.RATING_ID AS rating_id,
            STRING_AGG(fg.genre_id, ',') AS genres,
            STRING_AGG(df.DIRECTOR_ID, ',') AS directors
            FROM films f
            JOIN ratings r ON f.rating_id = r.rating_id
            LEFT JOIN film_genre fg ON f.film_id = fg.film_id
            LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.FILM_ID
            GROUP BY f.film_id
            ORDER BY f.film_id
            """;
    private static final String GET_FILM_BY_ID = """
            SELECT
                f.film_id,
                f.title,
                f.description,
                f.release_date,
                f.duration,
                f.RATING_ID AS rating_id,
                STRING_AGG(fg.genre_id, ',') AS genres,
                STRING_AGG(df.DIRECTOR_ID, ',') AS directors
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.rating_id
            LEFT JOIN film_genre fg ON f.film_id = fg.film_id
            LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.FILM_ID
            WHERE f.FILM_ID = ?
            GROUP BY f.film_id
            """;
    private static final String SET_LIKE_QUERY = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES (?,?)";
    private static final String SET_DISLIKE_QUERY = "DELETE FROM FILM_LIKES WHERE user_id = ? AND FILM_ID = ?";
    private static final String GET_POPULAR_FILMS_QUERY = """
                    SELECT
                        f.film_id,
                        f.title,
                        f.description,
                        f.release_date,
                        f.duration,
                        f.rating_id,
                        STRING_AGG(fg.genre_id, ',') AS genres,
                        STRING_AGG(df.DIRECTOR_ID, ',') AS directors
                    FROM films f
                    JOIN ratings r ON f.rating_id = r.rating_id
                    JOIN film_genre fg ON f.film_id = fg.film_id
                    LEFT JOIN film_likes fl ON fl.film_id = f.film_id
                    LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.FILM_ID
                    WHERE
                        (fg.genre_id = ? OR ? IS NULL)  -- Условие для фильтрации по жанру
                        AND (EXTRACT(YEAR FROM f.release_date) = ? OR ? IS NULL)  -- Условие для фильтрации по году
                    GROUP BY f.film_id
                    ORDER BY
                        COUNT(fl.user_id) DESC,
                        f.film_id ASC
                    LIMIT ?
            """;

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
    private static final String GET_RECOMMENDATION_QUERY = """
            SELECT\s
              f.film_id,\s
              f.title,\s
              f.description,\s
              f.release_date,\s
              f.duration,\s
              f.rating_id,\s
              STRING_AGG(fg.genre_id, ',') AS genres,\s
              STRING_AGG(df.DIRECTOR_ID, ',') AS directors
            FROM\s
              films f\s
              JOIN ratings r ON f.rating_id = r.rating_id\s
              JOIN film_genre fg ON f.film_id = fg.film_id\s
              LEFT JOIN film_likes fl ON fl.film_id = f.film_id\s
              LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.FILM_ID
            WHERE\s
              f.film_id IN (
                SELECT\s
                  fl.film_id\s
                FROM\s
                  FILM_LIKES fl\s
                WHEre\s
                  fl.user_id IN (
                    SELECT\s
                      fl1.USER_ID\s
                    FROM\s
                      FILM_LIKES fl\s
                      LEFT JOIN FILM_LIKES fl1 ON fl.FILM_ID = fl1.FILM_ID\s
                    WHERE\s
                      fl.USER_ID = ?\s
                      AND fl1.USER_ID <> ?\s
                    GROUP BY\s
                      fl1.USER_ID\s
                    ORDER BY\s
                      count(*) DESC\s
                    LIMIT\s
                      1
                  ) AND fl.FILM_ID NOT IN (
                    SELECT\s
                      fl.film_id\s
                    FROM\s
                      FILM_LIKES fl\s
                    WHERE\s
                      fl.user_id = ?
                  )
              )\s
            GROUP BY\s
              f.film_id\s
            ORDER BY\s
              COUNT(fl.user_id) DESC,\s
              f.film_id ASC
            """;
    private static final String GET_COMMON_FILMS_QUERY = """
            SELECT
            f.film_id,
            f.title,
            f.description,
            f.release_date,
            f.duration,
            f.rating_id,
            GROUP_CONCAT(DISTINCT fg.genre_id) AS genres,
            GROUP_CONCAT(DISTINCT df.director_id) AS directors
            FROM films f
            LEFT JOIN director_film df ON f.film_id = df.film_id
            LEFT JOIN film_genre fg ON f.film_id = fg.film_id
            LEFT JOIN film_likes fl ON f.film_id = fl.film_id
             WHERE fl.USER_ID = ?
             GROUP BY
             f.film_id
            """;
    private static final String GET_DIRECTORS_FILM_SORT_BY_YEAR = """
            SELECT\s
              f.film_id,\s
              f.title,\s
              f.description,\s
              f.release_date,\s
              f.duration,\s
              f.RATING_ID AS rating_id,\s
              STRING_AGG(fg.genre_id, ',') AS genres,\s
              STRING_AGG(df.DIRECTOR_ID, ',') AS directors\s
            FROM\s
              films f\s
              JOIN ratings r ON f.rating_id = r.rating_id\s
              LEFT JOIN film_genre fg ON f.film_id = fg.film_id\s
              LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.FILM_ID\s
            WHERE\s
              df.DIRECTOR_ID = ?\s
            GROUP BY\s
              f.film_id\s
            ORDER BY\s
              f.release_date
            """;
    private static final String GET_DIRECTOR_FILM_SORT_BY_LIKES = """
            SELECT\s
              f.film_id,\s
              f.title,\s
              f.description,\s
              f.release_date,\s
              f.duration,\s
              f.rating_id,\s
              GROUP_CONCAT(DISTINCT fg.genre_id) AS genres,\s
              GROUP_CONCAT(DISTINCT df.director_id) AS directors\s
            FROM\s
              films f\s
              JOIN director_film df ON f.film_id = df.film_id\s
              LEFT JOIN film_genre fg ON f.film_id = fg.film_id\s
              LEFT JOIN film_likes fl ON f.film_id = fl.film_id\s
            WHERE\s
              df.director_id = ?\s
            GROUP BY\s
              f.film_id,\s
              f.title,\s
              f.description,\s
              f.release_date,\s
              f.duration,\s
              f.rating_id\s
            ORDER BY\s
              COUNT(fl.user_id) DESC
            """;
    private static final String GET_FILM_BY_NAME_LIKE_QUERY = """
            SELECT
              f.film_id,
              f.title,
              f.description,
              f.release_date,
              f.duration,
              f.rating_id,
              STRING_AGG(fg.genre_id, ',') AS genres,
              STRING_AGG(df.DIRECTOR_ID, ',') AS directors
            FROM
              films f
              LEFT JOIN director_film df ON f.film_id = df.film_id
              LEFT JOIN film_genre fg ON f.film_id = fg.film_id
              LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            WHERE
            f.title LIKE ?
            GROUP BY
            f.film_id
            """;
    private static final String GET_FILM_BY_DIRECTOR_LIKE_QUERY = """
            SELECT
              f.film_id,
              f.title,
              f.description,
              f.release_date,
              f.duration,
              f.rating_id,
              GROUP_CONCAT(DISTINCT fg.genre_id) AS genres,
              GROUP_CONCAT(DISTINCT df.director_id) AS directors
            FROM
              films f
              LEFT JOIN director_film df ON f.film_id = df.film_id
              LEFT JOIN director d ON df.director_id = d.director_id
              LEFT JOIN film_genre fg ON f.film_id = fg.film_id
              LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            WHERE
            d.name LIKE ?
            """;
    private static final String GET_FILM_BY_NAME_OR_DIRECTOR_QUERY = """
            SELECT
              f.film_id,
              f.title,
              f.description,
              f.release_date,
              f.duration,
              f.rating_id,
              STRING_AGG(fg.genre_id, ',') AS genres,
              STRING_AGG(df.DIRECTOR_ID, ',') AS directors
            FROM
              films f
              LEFT JOIN director_film df ON f.film_id = df.film_id
              LEFT JOIN director d ON df.director_id = d.director_id
              LEFT JOIN film_genre fg ON f.film_id = fg.film_id
              LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            WHERE
            f.title LIKE ? OR d.name LIKE ?
            GROUP BY
            f.film_id
            """;
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = ?";

    @Override
    public Film save(Film film) {
        if (jdbc.queryForObject(EXIST_MPA_BY_ID_QUERY, Integer.class, film.getMpa()) == 0) {
            throw new MpaNotExistException("Рейтинга с id = " + film.getMpa() + " не существует");
        }
        if (film.getGenres() != null && !film.getGenres().stream()
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
        film.setId(filmId);
        if (film.getGenres() != null) {
            film.getGenres().forEach(x -> jdbc.update(INSERT_FILM_GENRE_QUERY, filmId, x));
        }
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(x -> jdbc.update(INSERT_FILM_DIRECTOR, filmId, x));
        }

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
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre ->
                    jdbc.update(INSERT_FILM_GENRE_QUERY, film.getId(), genre));
        }
        jdbc.update(DELETE_DIRECTOR_QUERY, film.getId());
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(x -> jdbc.update(INSERT_FILM_DIRECTOR, film.getId(), x));
        }
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


    public List<Film> getCommonFilms(Long userId, Long friendId) {
        try {
            return jdbc.query(GET_COMMON_FILMS_QUERY, filmRowMapper, userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbc.query(GET_ALL_FILMS_QUERY, filmRowMapper);
    }

    @Override
    public List<Film> getFilmsByName(String query) {
        return jdbc.query(GET_FILM_BY_NAME_LIKE_QUERY, filmRowMapper, "%" + query + "%");
    }

    @Override
    public List<Film> getFilmsByDirector(String query) {
        return jdbc.query(GET_FILM_BY_DIRECTOR_LIKE_QUERY, filmRowMapper, "%" + query + "%");
    }

    @Override
    public List<Film> getFilmsByNameAndDirector(String query) {
        return jdbc.query(GET_FILM_BY_NAME_OR_DIRECTOR_QUERY, filmRowMapper, "%" + query + "%", "%" + query + "%");
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
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        return jdbc.query(GET_POPULAR_FILMS_QUERY, filmRowMapper, genreId, genreId, year, year, count);
    }

    public List<Film> getRecommendations(Long userId) {
        return jdbc.query(GET_RECOMMENDATION_QUERY, filmRowMapper, userId, userId, userId);
    }

    @Override
    public List<Film> getDirectorFilmSortByYear(Long directorId) {
        return jdbc.query(GET_DIRECTORS_FILM_SORT_BY_YEAR, filmRowMapper, directorId);
    }

    @Override
    public List<Film> getDirectorFilmSortByLikes(Long directorId) {
        return jdbc.query(GET_DIRECTOR_FILM_SORT_BY_LIKES, filmRowMapper, directorId);
    }

    public List<Film> getFilmsByUserId(Long userId) {
        return jdbc.query(GET_COMMON_FILMS_QUERY, filmRowMapper, userId);
    }

    @Override
    public int getLikesCount(Long filmId) {
        return 0;

    @Override
    public void deleteFilmById(Long filmId) {
        if (existById(filmId)) {
            if ((jdbc.update(DELETE_FILM_QUERY, filmId) == 1)) {
                log.info("Фильм с id = {} удален", filmId);
            }
        } else {
            throw new FilmNotFoundException(
                    "Фильм с id = " + filmId + " не существует"
            );
        }
    }
}