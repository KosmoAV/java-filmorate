package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class MPA {
    private final Integer id;
    private final String name;
}
