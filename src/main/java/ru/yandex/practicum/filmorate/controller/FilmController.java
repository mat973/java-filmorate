package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Long, Film> filmMap = new HashMap<>();
    private final DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Long currentId = 0L;

    @PostMapping()
    public String createUser(@Valid @RequestBody FilmDto filmDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(" "));
        }
        Film film;
        try {
            filmDto.setId(currentId++);
            film = mapToFilm(filmDto);
        }catch (DateTimeParseException e){
            return "Такой даты не сущесвует.";
        }
        filmMap.put(film.getId(), film);
        return "Фильм был добавлен: " + film.toString();
    }

    @PutMapping()
    public String updateUser(@Valid @RequestBody FilmDto filmDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(" "));
        }
        Long id = filmDto.getId();
        if (id == null || !filmMap.containsKey(id)){
            return "Фильм " + (id == null ? "должен иеть id.":"с id" + id + " не сущесвет");
        }
        Film film;
        try {
            film = mapToFilm(filmDto);
        }catch (DateTimeParseException e){
            return "Такой даты не сущесвует.";
        }
        return "Фильм был обновлен : " + film.toString();
    }


    @GetMapping()
    public List<Film> getAllUsers(){
        return filmMap.values().stream().toList();
    }


    public Film mapToFilm(FilmDto filmDto) {
        LocalDate date = LocalDate.parse(filmDto.getReleaseDate(), FORMATER);
        Duration duration = Duration.ofMinutes(filmDto.getDuration());
        Film film = Film.builder().
                id(filmDto.getId()).
                description(filmDto.getDescription()).
                duration(duration).
                name(filmDto.getName())
                .releaseDate(date)
                .build();
        return film;
    }
}
