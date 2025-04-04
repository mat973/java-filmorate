package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.Genre;
import ru.yandex.practicum.filmorate.exeption.GenreNotExistException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper rowMapper;
    private static final String EXIST_GENRE_BY_ID_QUERY = "SELECT COUNT(*) FROM genre WHERE genre_id = ?";
    private static final String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genre  WHERE genre_id = ?";
    private static final String GET_ALL_GENRE_QUERY = "SELECT * FROM genre ORDER BY genre_id";

    @Autowired
    public GenreStorage(JdbcTemplate jdbc, GenreRowMapper rowMapper) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
    }

    public Genre getGenreById(Long genreId) {
        if (jdbc.queryForObject(EXIST_GENRE_BY_ID_QUERY, Integer.class, genreId) == 0) {
            throw new GenreNotExistException("Рейтинга с id = " + genreId + " не существует");
        }

        return jdbc.queryForObject(GET_GENRE_BY_ID_QUERY, rowMapper, genreId);

    }

    public List<Genre> getAllGenre() {
        return jdbc.query(GET_ALL_GENRE_QUERY, rowMapper);
    }

    public List<Genre> getGenresById(Set<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Collections.emptyList();
        }


        String inSql = genreIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT * FROM genre WHERE genre_id IN (" + inSql + ") ORDER BY genre_id";

        return jdbc.query(sql, rowMapper, genreIds.toArray());
    }
}
