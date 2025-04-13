package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final FilmService filmService;


    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public User createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Начало обработки запроса на создание пользователя: {}", userDto);
        return userService.createUser(userDto);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Начало обработки запроса на обновление пользователя: {}", userDto);
        return userService.upDateUser(userDto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.debug("Запрос на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getFilmById(@PathVariable Long id) {
        log.debug("Запрос на получение пользователя с id: {}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Выполнение запроса по добавлению в друзья от пользователям userId {}, польщователю friend userId {}",
                userId, friendId);
        userService.addFriendById(userId, friendId);
        userService.createEvent(userId, EventType.FRIEND, Operation.ADD, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Выполнение запроса по удалению из друзей от пользователям userId {}, польщователю friend userId {}",
                userId, friendId);
        userService.deleteFriendById(userId, friendId);
        userService.createEvent(userId, EventType.FRIEND, Operation.REMOVE, friendId);
    }

    @GetMapping("/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllFriends(@PathVariable Long userId) {
        log.info("Выполнение запроса для вывода списка друзей для пользователя с userId {}", userId);
        return userService.getAllFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherUserId) {
        log.info("Выполнение запроса по вывода списка общх друзей двух пользователий userid {} и otherUserId {}",
                userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }

    @GetMapping("{userId}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getRecommendation(@PathVariable Long userId) {
        log.info("Запрос на получение рекомендаций для пользователя с id {}", userId);
        return filmService.getRecommendation(userId);
    }

    @GetMapping("/{userId}/feed")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getUserEvents(@PathVariable Long userId) {
        return userService.getUserEvents(userId);
    }
}