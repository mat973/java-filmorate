package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getDirectors(){
        return directorService.getDirectors();
    }

    @GetMapping("/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable Long directorId){
        return directorService.getDirectorById(directorId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director saveDirector(@RequestBody Director director){
        return directorService.saveDirector(director);
    }

    @DeleteMapping("/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable Long directorId){
        directorService.deleteDirector(directorId);
    }
}

