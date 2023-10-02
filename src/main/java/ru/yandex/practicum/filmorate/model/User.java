package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {

    private Integer id;

    @EqualsAndHashCode.Exclude
    private final String email;

    @EqualsAndHashCode.Exclude
    private final String login;

    @NonNull
    @EqualsAndHashCode.Exclude
    private String name;

    @EqualsAndHashCode.Exclude
    private final LocalDate birthday;
}
