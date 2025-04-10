package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDbStorage {

    List<Director> getDirectors();

    Director getDirectorById(Long directorId);

    Director updateDirector(Director director);

    Director saveDirector(Director director);

    void deleteDirector(Long directorId);

    Boolean existDirector(Long directorId);
}
