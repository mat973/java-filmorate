package ru.yandex.practicum.filmorate.storage.mpa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.Mpa;
import ru.yandex.practicum.filmorate.exception.MpaNotExistException;

import java.util.List;

@Component
public class MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper rowMapper;
    private static final String EXIST_MPA_BY_ID_QUERY = "SELECT COUNT(*) FROM ratings WHERE rating_id = ?";
    private static final String GET_MPA_BY_ID_QUERY = "SELECT * FROM ratings  WHERE rating_id = ?";
    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM ratings ORDER BY rating_id";


    @Autowired
    public MpaStorage(JdbcTemplate jdbc, MpaRowMapper rowMapper) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
    }

    public Mpa getMpaById(Long mpaId) {
        if (jdbc.queryForObject(EXIST_MPA_BY_ID_QUERY, Integer.class, mpaId) == 0) {
            throw new MpaNotExistException("Рейтинга с id = " + mpaId + " не существует");
        }
        return jdbc.queryForObject(GET_MPA_BY_ID_QUERY, rowMapper, mpaId);
    }

    public List<Mpa> getAllMpa() {
        return jdbc.query(GET_ALL_MPA_QUERY, rowMapper);
    }

    public Boolean existMpa(Integer mpaId) {
        return jdbc.queryForObject(EXIST_MPA_BY_ID_QUERY, Integer.class, mpaId) != 0;
    }
}
