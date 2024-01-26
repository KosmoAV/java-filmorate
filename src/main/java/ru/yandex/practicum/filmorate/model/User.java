package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {

    private Integer id;

    private final String email;

    private final String login;

    private String name;

    private final LocalDate birthday;

    private final Set<Integer> friends;

    @ConstructorProperties({"email","login","name","birthday"})
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this(email, login, name, birthday);
        this.id = id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }
}