package ru.yandex.practicum.filmorate.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users;

    Integer userId;

    public UserController() {
        users = new HashMap<>();
        userId = 0;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        log.info("Получен запрос на создание пользователя");
        validateUser(user);
        users.put(getId(user), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        log.info("Получен запрос на обновление пользователя");
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    private Integer getId(User user) {
        int id = ++userId;
        user.setId(id);
        return id;
    }

    private void validateUser(User user) throws ValidationException {

        if (user.getId() != null && !users.containsKey(user.getId())) {
            log.error("Выброшено исключение");
            throw new ValidationException("Некорректный Id пользователя");
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
           log.error("Выброшено исключение");
           throw new ValidationException("Некорректный e-mail");
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
           log.error("Выброшено исключение");
           throw new ValidationException("Некорректный логин");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
           log.error("Выброшено исключение");
           throw new ValidationException("Некорректная дата рождения");
        }

        if (user.getName() == null || user.getName().isBlank()) {
           user.setName(user.getLogin());
        }
    }
}
