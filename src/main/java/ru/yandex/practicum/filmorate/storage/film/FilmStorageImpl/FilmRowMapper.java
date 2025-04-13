package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Film.builder()
                .id(rs.getLong("film_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(Duration.ofMinutes(rs.getInt("duration")))
                .mpa(rs.getInt("rating_id"))
                .genres(rs.getString("genres") == null ?
                        null : Arrays.stream((rs.getString("genres").split(","))).sequential()
                        .map(Integer::parseInt).collect(Collectors.toList()))
                .directors(rs.getString("directors") == null ?
                        null : Arrays.stream((rs.getString("directors").split(","))).sequential()
                        .map(Long::parseLong).collect(Collectors.toList()))
                .build();
    }
}