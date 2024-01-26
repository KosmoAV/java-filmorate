package ru.yandex.practicum.filmorate.storage.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres() {
        try {
            return jdbcTemplate.query("SELECT * " +
                    "FROM genres ORDER BY id;", genreRowMaper());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    public Genre getGenre(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * " +
                    "FROM genres WHERE id = ?;", genreRowMaper(), id);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    private RowMapper<Genre> genreRowMaper() {
        return (rs, rowNum) -> {
            return new Genre(rs.getInt("id"), rs.getString("genre"));
        };
    }
}
