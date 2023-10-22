package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserControllerTests {

    UserController userController;

    @BeforeEach
    public void initTests() {
        UserStorage userStorage = new InMemoryUserStorage();
        userController = new UserController(userStorage, new UserService(userStorage));
    }

    @Test
    public void addUserWithIncorrectEmail() {
        User user1 = new User(" yandex.ru ", "login", "name", LocalDate.now().minusYears(25));
        User user2 = new User(" ", "login", "name", LocalDate.now().minusYears(25));

        try {
            userController.createUser(user1);
        } catch (ValidationException exception) {
            assertEquals("400 BAD_REQUEST \"Некорректный e-mail\"", exception.getMessage());
        }

        try {
            userController.createUser(user2);
        } catch (ValidationException exception) {
            assertEquals("400 BAD_REQUEST \"Некорректный e-mail\"", exception.getMessage());
        }

        assertEquals(0, userController.getUsers().size(), "Неверное количество пользователей");
    }

    @Test
    public void addUserWithCorrectEmail() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().minusYears(25));

        try {
            userController.createUser(user);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userController.getUsers().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addUserWithIncorrectLogin() {
        User user1 = new User("name@yandex.ru", "login me", "name", LocalDate.now().minusYears(25));
        User user2 = new User("name@yandex.ru", "  ", "name", LocalDate.now().minusYears(25));

        try {
            userController.createUser(user1);
        } catch (ValidationException exception) {
            assertEquals("400 BAD_REQUEST \"Некорректный логин\"", exception.getMessage());
        }

        try {
            userController.createUser(user2);
        } catch (ValidationException exception) {
            assertEquals("400 BAD_REQUEST \"Некорректный логин\"", exception.getMessage());
        }

        assertEquals(0, userController.getUsers().size(), "Неверное количество пользователей");
    }

    @Test
    public void addUserWithCorrectLogin() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().minusYears(25));

        try {
            userController.createUser(user);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userController.getUsers().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addUserWithIncorrectBirthday() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().plusDays(1));

        try {
            userController.createUser(user);
        } catch (ValidationException exception) {
            assertEquals("400 BAD_REQUEST \"Некорректная дата рождения\"", exception.getMessage());
        }

        assertEquals(0, userController.getUsers().size(), "Неверное количество пользователей");
    }

    @Test
    public void addUserWithCorrectBirthday() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().minusDays(1));

        try {
            userController.createUser(user);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userController.getUsers().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addUserWithoutName() {
        User user = new User("name@yandex.ru", "login", "  ", LocalDate.now().minusDays(1));

        try {
            userController.createUser(user);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals("login", userController.getUsers().get(0).getName(), "Некорректное имя");
    }
}
