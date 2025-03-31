package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;

@Component
public class FilmRowMapper implements RowMapper<Film>{

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(Duration.ofMinutes(rs.getInt("duration")))
                .mpa(Rating.valueOf(rs.getString("mpa_name")))
                .genres(new ArrayList<>())  // Жанры будут заполнены отдельно
                .build();
    }
}
