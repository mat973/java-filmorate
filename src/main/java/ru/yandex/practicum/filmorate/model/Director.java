package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Director {
    private Long id;
    private String name;

    public Director(Long id) {
        this.id = id;
    }
}
