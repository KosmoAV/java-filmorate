package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationException;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
public class UserControllerTests {

    UserController userController;

    @BeforeEach
    public void initTests() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    public void addUserWithIncorrectEmail() {
        User user1 = new User(" yandex.ru ", "login", "name", LocalDate.now().minusYears(25));
        User user2 = new User(" ", "login", "name", LocalDate.now().minusYears(25));

        ValidationException e = assertThrows(ValidationException.class, () -> userController.createUser(user1),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный e-mail\"", e.getMessage());

        e = assertThrows(ValidationException.class, () -> userController.createUser(user2),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный e-mail\"", e.getMessage());

        assertEquals(0, userController.getUsers().size(), "Неверное количество пользователей");
    }

    @Test
    public void addUserWithCorrectEmail() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().minusYears(25));

        assertDoesNotThrow(() -> userController.createUser(user), "Выброшено исключение");

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userController.getUsers().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addUserWithIncorrectLogin() {
        User user1 = new User("name@yandex.ru", "login me", "name", LocalDate.now().minusYears(25));
        User user2 = new User("name@yandex.ru", "  ", "name", LocalDate.now().minusYears(25));

        ValidationException e = assertThrows(ValidationException.class, () -> userController.createUser(user1),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный логин\"", e.getMessage());

        e = assertThrows(ValidationException.class, () -> userController.createUser(user2),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный логин\"", e.getMessage());

        assertEquals(0, userController.getUsers().size(), "Неверное количество пользователей");
    }

    @Test
    public void addUserWithCorrectLogin() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().minusYears(25));

        assertDoesNotThrow(() -> userController.createUser(user), "Выброшено исключение");

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userController.getUsers().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addUserWithIncorrectBirthday() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().plusDays(1));

        ValidationException e = assertThrows(ValidationException.class, () -> userController.createUser(user),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректная дата рождения\"", e.getMessage());

        assertEquals(0, userController.getUsers().size(), "Неверное количество пользователей");
    }

    @Test
    public void addUserWithCorrectBirthday() {
        User user = new User("name@yandex.ru", "login", "name", LocalDate.now().minusDays(1));

        assertDoesNotThrow(() -> userController.createUser(user), "Выброшено исключение");

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userController.getUsers().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addUserWithoutName() {
        User user = new User("name@yandex.ru", "login", "  ", LocalDate.now().minusDays(1));

        assertDoesNotThrow(() -> userController.createUser(user), "Выброшено исключение");

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals("login", userController.getUsers().get(0).getName(), "Некорректное имя");
    }

    @Test
    public void updateUserWithIncorrectId() {

        User user1 = new User("first@yandex.ru", "login", "name", LocalDate.now().minusYears(25));
        assertDoesNotThrow(() -> userController.createUser(user1), "Выброшено исключение");

        User user2 = new User("second@yandex.ru", "login", "name", LocalDate.now().minusYears(25));

        user2.setId(null);
        ValidationException e = assertThrows(ValidationException.class, () -> userController.updateUser(user2),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный запрос или id пользователя\"", e.getMessage());

        user2.setId(0);
        e = assertThrows(ValidationException.class, () -> userController.updateUser(user2),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный id пользователя\"", e.getMessage());

        assertEquals(1, userController.getUsers().size(), "Неверное количество пользователей");
        assertEquals(1, userController.getUsers().get(0).getId(), "Некорректный Id");
    }
}
