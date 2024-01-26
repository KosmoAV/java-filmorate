package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {

    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void initTests() {

    }

    @Test
    public void createAndFindFilmById() {

        Film newFilm = new Film("1", "1", LocalDate.of(1900, 1, 1),
                90, new MPA(2, "PG"));
        newFilm.setId(1);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        filmStorage.addFilm(newFilm);

        Film savedFilm = filmStorage.getFilm(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    public void getAllFilms() {

        Film newFilm1 = new Film("1", "1", LocalDate.of(1900, 1, 1),
                90, new MPA(2, "PG"));
        newFilm1.setId(1);
        Film newFilm2 = new Film("2", "2", LocalDate.of(1900, 2, 2),
                90, new MPA(1, "G"));
        newFilm2.setId(2);

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        filmStorage.addFilm(newFilm1);
        filmStorage.addFilm(newFilm2);

        List<Film> savedUser = filmStorage.getFilms();

        assertThat(savedUser.get(0))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm1);

        assertThat(savedUser.get(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm2);
    }

    @Test
    public void updateFilm() {

        Film oldFilm = new Film("1", "1", LocalDate.of(1900, 1, 1),
                90, new MPA(2, "PG"));
        oldFilm.setId(1);
        Film newFilm = new Film("new", "new", LocalDate.of(1900, 2, 2),
                90, new MPA(1, "G"));
        newFilm.setId(1);

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        filmStorage.addFilm(oldFilm);
        filmStorage.updateFilm(newFilm);

        Film savedFilm = filmStorage.getFilm(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }
}
