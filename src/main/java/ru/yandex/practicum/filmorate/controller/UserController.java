package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ExceptionDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exeption.DataNotExistException;
import ru.yandex.practicum.filmorate.exeption.LoginContainSpaceException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> userMap = new HashMap<>();
    private final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Long currentId = 1L;

    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        log.debug("Начало обработки запроса на создание пользователя: {}", userDto);

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(" "));
            log.warn("Ошибка валидации при создании пользователя: {}", errorMessage);
            return new ResponseEntity<>(new ExceptionDto(errorMessage), HttpStatus.BAD_REQUEST);
        }

        User user;
        try {
            userDto.setId(currentId++);
            user = mapToUser(userDto);
            log.info("Пользователь успешно создан: {}", user);
        } catch (DateTimeParseException e) {
            log.error("Ошибка парсинга даты рождения: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto("Такой даты не существует."), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        userMap.put(user.getId(), user);
        log.trace("Пользователь добавлен в хранилище. Текущее количество пользователей: {}", userMap.size());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        log.debug("Начало обработки запроса на обновление пользователя: {}", userDto);

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(" "));
            log.warn("Ошибка валидации при обновлении пользователя: {}", errorMessage);
            return new ResponseEntity<>(new ExceptionDto(errorMessage), HttpStatus.BAD_REQUEST);
        }

        Long id = userDto.getId();
        if (id == null || !userMap.containsKey(id)) {
            String errorMessage = "Пользователь " + (id == null ? "должен иметь id." : "с id " + id + " не существует.");
            log.warn("Ошибка при обновлении пользователя: {}", errorMessage);
            return new ResponseEntity<>(new ExceptionDto(errorMessage), HttpStatus.NOT_FOUND);
        }

        User user;
        try {
            user = mapToUser(userDto);
            log.info("Пользователь успешно обновлён: {}", user);
        } catch (DateTimeParseException e) {
            log.error("Ошибка парсинга даты рождения: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto("Такой даты не существует."), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        log.debug("Запрос на получение списка всех пользователей");
        log.trace("Текущее количество пользователей: {}", userMap.size());
        return new ResponseEntity<>(userMap.values().stream().toList(), HttpStatus.OK);
    }

    private User mapToUser(UserDto userDto) {
        log.debug("Начало преобразования UserDto в User: {}", userDto);

        if (containSpace(userDto.getLogin())) {
            log.warn("Логин содержит пробелы: {}", userDto.getLogin());
            throw new LoginContainSpaceException();
        }

        log.info("Парсинг даты рождения: {}", userDto.getBirthday());
        LocalDate birthday = LocalDate.parse(userDto.getBirthday(), formater);

        if (birthday.isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть позже текущей даты: {}", birthday);
            throw new DataNotExistException("Дата рождения не может быть позже текущей даты.");
        }

        if (userDto.getName() == null) {
            log.info("Имя пользователя не указано, используется логин: {}", userDto.getLogin());
            userDto.setName(userDto.getLogin());
        }

        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .name(userDto.getName())
                .birthday(birthday)
                .build();

        log.trace("Пользователь успешно преобразован: {}", user);
        return user;
    }

    private boolean containSpace(String s) {
        log.trace("Проверка строки на наличие пробелов: {}", s);
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                log.debug("Найден пробел в строке: {}", s);
                return true;
            }
        }
        return false;
    }
}