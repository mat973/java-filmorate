package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.Genre;
import ru.yandex.practicum.filmorate.dto.Mpa;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                .mpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")))
                .genres(convertGenre(rs.getString("genres"), rs.getString("genres_name")))
                .directors(convertDirector(rs.getString("directors"), rs.getString("directors_name")))
                .build();
    }

    private List<Genre> convertGenre(String id, String name){
        List<Genre> genres = new ArrayList<>();
        if (id == null){
            return genres;
        }
        String[] ids = id.split(",");
        String[] names = name.split(",");

        for (int i = 0; i < ids.length; i++) {
            genres.add(new Genre(Integer.valueOf(ids[i]), names[i]));
        }
        return genres;
    }

    private List<Director> convertDirector(String id, String name){
        List<Director> directors = new ArrayList<>();
        if (id == null){
            return directors;
        }
        String[] ids = id.split(",");
        String[] names = name.split(",");

        for (int i = 0; i < ids.length; i++) {
            directors.add(new Director(Long.valueOf(ids[i]), names[i]));
        }
        return directors;
    }
}