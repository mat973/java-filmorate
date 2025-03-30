package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class FilmRowMapper implements RowMapper<Film>{

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(parseInterval(rs.getString("duration")))
                .rating
                .build();
    }


    private Duration parseInterval(String pgInterval) {
        String[] parts = pgInterval.split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);

        return Duration.ofHours(hours)
                .plusMinutes(minutes);
    }
}
