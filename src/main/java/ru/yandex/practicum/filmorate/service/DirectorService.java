package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    public List<Director> getDirectors() {

    }

    public Director getDirectorById(Long directorId) {
        
    }

    public Director saveDirector(Director director) {
    }

    public void deleteDirector(Long directorId) {
    }
}
