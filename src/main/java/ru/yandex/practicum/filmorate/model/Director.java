package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Director {
    private Long directorId;
    private String name;

    public Director(Long directorId) {
        this.directorId = directorId;
    }
}
