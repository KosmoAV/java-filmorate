package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPA.MPAStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MPAStorageTests {

    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void initTests() {

    }
    @Test
    public void getMPA() {

        MPAStorage mpaStorage = new MPAStorage(jdbcTemplate);

        List<MPA> mpas = mpaStorage.getMPA();

        assertEquals(5, mpas.size(), "Неверное количество MPA");

        assertThat(new MPA(3, "PG-13"))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpas.get(2));
    }

    @Test
    public void getMPAById() {

        MPAStorage mpaStorage = new MPAStorage(jdbcTemplate);

        assertThat(new MPA(3, "PG-13"))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpaStorage.getMPA().get(2));
    }
}
