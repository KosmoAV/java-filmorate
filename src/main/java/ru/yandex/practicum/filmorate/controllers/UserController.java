package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserStorage userStorage;
    private UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
       this.userStorage = userStorage;
       this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        log.info("Получен запрос на создание пользователя");
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        log.info("Получен запрос на обновление пользователя");
        return userStorage.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info("Получен запрос на получение данных пользователя id = " + id);
        return userStorage.getUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос от пользователя id = {} на добавления в друзья пользователя id = {}", id, friendId);
        userService.addFriend(id, friendId);
        userService.addFriend(friendId, id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос от пользователя id = {} на исключение из друзей пользователя id = {}", id, friendId);
        userService.deleteFriend(id, friendId);
        userService.deleteFriend(friendId, id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Получен запрос на получение всех друзей пользователя id = {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Получен запрос на получение всех общих друзей пользователей id = {} и id = {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ValidationException e) {
        log.info(e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final ResponseStatusException e) {
        log.info(e.getMessage());
        return new ErrorResponse("По запросу ничего не найдено", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Exception e) {
        log.info(e.getMessage());
        return new ErrorResponse("Выброшенно исключение", e.getMessage());
    }
}
