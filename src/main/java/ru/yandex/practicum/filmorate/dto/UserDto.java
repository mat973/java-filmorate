package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @Email(message = "Введите верный формать Email.")
    private String email;
    @NotNull(message = "Логин не должен существовать.")
    @NotBlank(message = "Логин не должен быть пустым.")
    private String login;
    private String name;
    @NotNull(message = "Дата не должна быть пустая.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата должна быть в формате yyyy-MM-dd.")
    private String birthday;
}
