package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {

    private Integer id;

    private final String name;

    private final String description;

    private final LocalDate releaseDate;

    private final Integer  duration;

    private final List<Genre> genres;

    private final MPA mpa;

    private final Set<Integer> likes;

    @ConstructorProperties({"name","description","releaseDate","duration", "mpa"})
    public Film(String name, String description, LocalDate releaseDate, Integer duration, MPA mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = new ArrayList<>();
        this.likes = new HashSet<>();
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, MPA mpa) {
        this(name, description, releaseDate, duration, mpa);
        this.id = id;
    }

    public Film setId(Integer id) {
        this.id = id;
        return this;
    }
}
