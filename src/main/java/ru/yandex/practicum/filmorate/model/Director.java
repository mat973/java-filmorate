package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Director {
    private Long directorId;
    private String name;

    public Director(Long directorId) {
        this.directorId = directorId;
    }
}
