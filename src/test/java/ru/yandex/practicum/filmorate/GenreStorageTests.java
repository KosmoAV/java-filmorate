package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTests {

    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void initTests() {

    }
    @Test
    public void getGenre() {

        GenreStorage genreStorage = new GenreStorage(jdbcTemplate);

        assertThat(new Genre(1, "Комедия"))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genreStorage.getGenre(1));
    }

    @Test
    public void getGenres() {

        GenreStorage genreStorage = new GenreStorage(jdbcTemplate);

        List<Genre> genres = genreStorage.getGenres();

        assertEquals(6, genres.size(), "Неверное количество жанров");

        assertThat(new Genre(3, "Мультфильм"))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genres.get(2));

    }
}
