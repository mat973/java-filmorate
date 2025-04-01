package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/{genreId}")
    public Genre getGenreById(@PathVariable Long genreId) {
        log.info("Запрос на получение жанра по id {}.", genreId);
        return genreService.getGenreById(genreId);
    }

    @GetMapping
    public List<Genre> getAllGenre() {
        log.info("Запрос на получение всех жанров.");
        return genreService.getAllGenre();
    }
}
