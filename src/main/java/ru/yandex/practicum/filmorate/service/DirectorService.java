package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorMustHaveNameException;
import ru.yandex.practicum.filmorate.exception.DirectorNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    public List<Director> getDirectors() {
        return directorDbStorage.getDirectors();
    }

    public Director getDirectorById(Long directorId) {
        if (!directorDbStorage.existDirector(directorId)) {
            throw new DirectorNotExistException("Директора с id " + directorId + " не существует.");
        }
        return directorDbStorage.getDirectorById(directorId);
    }

    public Director saveDirector(Director director) {
        if (director.getName() == null) {
            throw new DirectorMustHaveNameException("У директора должно быть имя");
        }
        return directorDbStorage.saveDirector(director);
    }

    public void deleteDirector(Long directorId) {
        if (!directorDbStorage.existDirector(directorId)) {
            throw new DirectorNotExistException("Директора с id " + directorId + " не существует.");
        }
        directorDbStorage.deleteDirector(directorId);
    }

    public Director updateDirector(Director director) {
        if (director.getId() == null || !directorDbStorage.existDirector(director.getId())) {
            throw new DirectorNotExistException("Директора с id " + director.getId() + " не существует.");
        }
        if (director.getName() == null) {
            throw new DirectorMustHaveNameException("У директора должно быть имя");
        }
        return directorDbStorage.updateDirector(director);
    }

    public List<Director> getDirectorsByFilmId(Long filmId) {
        return directorDbStorage.getDirectorsByFilmId(filmId);
    }

    public Boolean existDirector(Long directorId) {
        return directorDbStorage.existDirector(directorId);
    }
}
