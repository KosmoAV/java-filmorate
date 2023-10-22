package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {

    private Integer id;

    private final String name;

    private final String description;

    private final LocalDate releaseDate;

    private final Integer  duration;

    private final Integer rate;

    private final Set<Integer> likes;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = 0;
        this.likes = new HashSet<>();
    }
}
