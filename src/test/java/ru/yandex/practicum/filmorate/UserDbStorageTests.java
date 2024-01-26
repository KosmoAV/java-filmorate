package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {

    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void initTests() {
        jdbcTemplate.update("DELETE FROM users;");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    public void createAndFindUserById() {

        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newUser.setId(1);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        userStorage.createUser(newUser);

        User savedUser = userStorage.getUser(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void getAllUsers() {

        User newUser1 = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newUser1.setId(1);
        User newUser2 = new User("2@2", "2", "2", LocalDate.of(1990, 1, 1));
        newUser2.setId(2);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        userStorage.createUser(newUser1);
        userStorage.createUser(newUser2);

        List<User> savedUser = userStorage.getUsers();

        assertThat(savedUser.get(0))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser1);

        assertThat(savedUser.get(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser2);
    }

    @Test
    public void getUsersById() {

        User newUser1 = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newUser1.setId(1);
        User newUser2 = new User("2@2", "2", "2", LocalDate.of(1990, 1, 1));
        newUser2.setId(2);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        userStorage.createUser(newUser1);
        userStorage.createUser(newUser2);

        List<User> savedUser = userStorage.getUsers(2,1);

        assertThat(savedUser.get(0))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser1);

        assertThat(savedUser.get(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser2);
    }

    @Test
    public void addAndDeleteFriend() {

        User newUser1 = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newUser1.setId(1);
        User newUser2 = new User("2@2", "2", "2", LocalDate.of(1990, 1, 1));
        newUser2.setId(2);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        userStorage.createUser(newUser1);
        userStorage.createUser(newUser2);

        userStorage.addFriend(newUser1.getId(), newUser2.getId());

        int friendId = userStorage.getUser(1).getFriends().toArray(new Integer[1])[0];

        assertEquals(2,friendId, "Неверный id друга");

        userStorage.deleteFriend(newUser1.getId(), newUser2.getId());

        boolean empty = userStorage.getUser(1).getFriends().isEmpty();

        assertTrue(empty, "Неверное количество друзей");
    }

    @Test
    public void getCommonFriend() {

        User newUser1 = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newUser1.setId(1);
        User newUser2 = new User("2@2", "2", "2", LocalDate.of(1990, 2, 2));
        newUser2.setId(2);
        User newUser3 = new User("3@3", "3", "3", LocalDate.of(1990, 3, 3));
        newUser3.setId(3);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        userStorage.createUser(newUser1);
        userStorage.createUser(newUser2);
        userStorage.createUser(newUser3);

        userStorage.addFriend(newUser1.getId(), newUser2.getId());
        userStorage.addFriend(newUser3.getId(), newUser2.getId());

        List<User> commonFriends = userStorage.getCommonFriends(newUser1.getId(), newUser3.getId());

        assertThat(commonFriends.get(0))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser2);
    }
}
