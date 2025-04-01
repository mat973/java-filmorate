package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/{mpaId}")
    public Mpa getMpaById(@PathVariable Long mpaId) {
        log.info("Вызван запрос по получении рейтинга с id {}", mpaId);
        return mpaService.getMpaById(mpaId);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Запрос на получение списка всех рейтингов");
        return mpaService.getAllMpa();
    }
}
