package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getDirectors() {
        log.info("Запрос на получние списка всех режисеров");
        return directorService.getDirectors();
    }

    @GetMapping("/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable Long directorId) {
        log.info("Запрос на получение режисера с id {}", directorId);
        return directorService.getDirectorById(directorId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director saveDirector(@RequestBody Director director) {
        log.info("Запрос на создание нового ржисера с имнем {}", director.getName());
        return directorService.saveDirector(director);
    }

    @DeleteMapping("/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable Long directorId) {
        log.info("Запрос на удаление директора с id {}", directorId);
        directorService.deleteDirector(directorId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@RequestBody Director director) {
        log.info("Запрос на обналвение директора с id {}", director.getName());
        return directorService.updateDirector(director);
    }
}

