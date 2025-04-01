package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.Genre;
import ru.yandex.practicum.filmorate.exeption.GenreNotExistException;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Set;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(Long genreId) {
        if (genreId == null) {
            throw new GenreNotExistException("Жанр не может бытьпустмы");
        }
        return genreStorage.getGenreById(genreId);
    }

    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    public List<Genre> getGenresByIds(Set<Long> genreIds) {
        return genreStorage.getGenresById(genreIds);
    }
}
