package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) throws ValidationException {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withTableName("users")
                    .usingGeneratedKeyColumns("id");

        Map<String, Object> params = Map.of("email", user.getEmail(),
                                            "login", user.getLogin(),
                                            "name", user.getName(),
                                            "birthday", user.getBirthday());

        Number id;

        try {
            id = simpleJdbcInsert.executeAndReturnKey(params);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return getUser(id.intValue());
    }

    @Override
    public User updateUser(User user) throws ValidationException {

        try {
            jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return getUser(user.getId());
    }

    @Override
    public List<User> getUsers() {

        try {
            return jdbcTemplate.queryForObject("SELECT id, email, login, name, birthday, friends_id " +
                    "FROM users AS u " +
                    "LEFT OUTER JOIN users_friends AS f " +
                    "ON u.id = f.user_id " +
                    "ORDER BY u.id;", usersRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    @Override
    public List<User> getUsers(Integer...ids) {

        StringBuilder str = new StringBuilder(2 * ids.length);

        for (int i = 0; i < ids.length; i++) {
            str.append("?,");
        }
        str.deleteCharAt(str.length() - 1);

        try {
            return jdbcTemplate.queryForObject("SELECT id, email, login, name, birthday, friends_id " +
                    "FROM users AS u " +
                    "LEFT OUTER JOIN users_friends AS f " +
                    "ON u.id = f.user_id WHERE u.id IN (" + str + ") " +
                    "ORDER BY u.id;", usersRowMapper(), ids);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    @Override
    public User getUser(Integer id) {

        try {
            return jdbcTemplate.queryForObject("SELECT id, email, login, name, birthday, friends_id " +
                    "FROM users AS u " +
                    "LEFT OUTER JOIN users_friends AS f " +
                    "ON u.id = f.user_id WHERE u.id = ?;", userRowMapper(), id);

        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    @Override
    public void addFriend(Integer id, Integer otherId) {
        try {
            jdbcTemplate.update("INSERT INTO users_friends (user_id, friends_id) VALUES (?, ?);", id, otherId);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer otherId) {
        try {
            jdbcTemplate.update("DELETE FROM users_friends WHERE user_id = ? AND friends_id = ?;", id, otherId);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, email, login, name, birthday, friends_id FROM " +
                    "(SELECT * FROM users WHERE id IN " +
                    "(SELECT one.friends_id " +
                    "FROM ((SELECT * FROM users_friends WHERE user_id = ?) AS one " +
                    "INNER JOIN (SELECT * FROM users_friends WHERE user_id = ?) AS two " +
                    "ON one.friends_id = two.friends_id))) AS common " +
                    "LEFT OUTER JOIN users_friends ON common.id = users_friends.user_id " +
                    "ORDER BY common.id;", usersRowMapper(), id, otherId);

        } catch (DataAccessException e) {
            return List.of();
        }
    }

    private RowMapper<List<User>> usersRowMapper() {
        return (rs, rowNum) -> {

            List<User> users = new ArrayList<>();
            boolean done = false;

            do {

                User user = new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
                );

                do {

                    int friendId = rs.getInt("friends_id");
                    if (friendId == 0) {
                        done = !rs.next();
                        break;
                    }

                    user.getFriends().add(friendId);

                    done = !rs.next();

                } while (!done && (rs.getInt("id") == user.getId()));

                users.add(user);

            } while (!done);

            return users;
        };
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {

            User user = new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );

            do {

                int friendId = rs.getInt("friends_id");
                if (friendId == 0) break;

                user.getFriends().add(friendId);

            } while (rs.next());

            return user;
        };
    }
}
