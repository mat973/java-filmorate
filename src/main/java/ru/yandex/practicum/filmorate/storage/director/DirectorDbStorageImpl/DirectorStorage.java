package ru.yandex.practicum.filmorate.storage.director.DirectorDbStorageImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorStorage implements DirectorDbStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Director> rowMapper;

    private static final String EXIST_BY_ID_QUERY = "SELECT COUNT(*) > 0 FROM director WHERE director_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM director";
    private static final String INSERT_QUERY = "INSERT INTO director (name) values (?)";
    private static final String GET_DIRECTOR_BY_ID = "SELECT * FROM director WHERE director_id = ?";
    private static final String UPDATE_QUERY = "UPDATE director SET name = ? where director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM director WHERE ?";
    private static final String GET_DIRECTOR_BY_FILM_ID_QUERY = """
                    SELECT d.DIRECTOR_ID , d.NAME
                    from DIRECTOR d
                    join DIRECTOR_FILM df  on d.director_id = df.director_id
                    where df.film_id = ?
            """;

    @Override
    public List<Director> getDirectors() {
        return jdbc.query(FIND_ALL_QUERY, rowMapper);
    }

    @Override
    public Director getDirectorById(Long directorId) {
        return jdbc.queryForObject(GET_DIRECTOR_BY_ID, rowMapper, directorId);
    }

    @Override
    public Director updateDirector(Director director) {
        jdbc.update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public Director saveDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        jdbc.update(DELETE_QUERY, directorId);
    }

    @Override
    public Boolean existDirector(Long directorId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(EXIST_BY_ID_QUERY, Boolean.class, directorId));
    }

    @Override
    public List<Director> getDirectorsByFilmId(Long filmId) {
        return jdbc.query(GET_DIRECTOR_BY_FILM_ID_QUERY, rowMapper, filmId);
    }
}
