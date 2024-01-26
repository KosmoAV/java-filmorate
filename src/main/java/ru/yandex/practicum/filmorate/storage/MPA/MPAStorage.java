package ru.yandex.practicum.filmorate.storage.MPA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Repository
public class MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPAStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MPA> getMPA() {
        try {
            return jdbcTemplate.query("SELECT * " +
                    "FROM ratings ORDER BY id;", mapRowMaper());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    public MPA getMPA(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * " +
                    "FROM ratings WHERE id = ?;", mapRowMaper(), id);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка доступа к БД");
        }
    }

    private RowMapper<MPA> mapRowMaper() {
        return (rs, rowNum) -> {
            return new MPA(rs.getInt("id"), rs.getString("mpa"));
        };
    }
}
