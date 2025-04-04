package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}