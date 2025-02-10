package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.log;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Long, User> userMap = new HashMap<>();
    private final DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Long currentId = 0L;

    @PostMapping()
    public User createUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()){
//            return bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
//                    .collect(Collectors.joining(" "));
//        }
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("Неверные параметры");
        }
        User user;
//        try {
            userDto.setId(currentId++);
            user = mapToUser(userDto);
//        }catch (DateTimeParseException e){
//            return "Такой даты не сущесвует.";
//        }
        userMap.put(user.getId(), user);
        return user;
    }

    @PutMapping()
    public String updateUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(" "));
        }
        Long id = userDto.getId();
        if (id == null || !userMap.containsKey(id)){
            return "Пользователь " + (id == null ? "должен иеть id.":"с id" + id + " не сущесвет");
        }
        User user;
        try {
            user = mapToUser(userDto);
        }catch (DateTimeParseException e){
            return "Такой даты не сущесвует.";
        }
        return "Пользователь был обновлен: " + user.toString();
    }


    @GetMapping()
    public List<User> getAllUsers(){
        return userMap.values().stream().toList();
    }


    public User mapToUser(UserDto userDto) {
        log.info(userDto.getBirthday());
        LocalDate birthday = LocalDate.parse(userDto.getBirthday(), FORMATER);
        User user = User.builder().
        id(userDto.getId()).
        email(userDto.getEmail()).
        login(userDto.getLogin()).
        name(userDto.getName()).
                birthday(birthday)
                .build();
        return user;
    }
}
