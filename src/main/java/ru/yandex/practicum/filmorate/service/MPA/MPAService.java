package ru.yandex.practicum.filmorate.service.MPA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPA.MPAStorage;

import java.util.List;

@Service
public class MPAService {

    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MPA> getMPA() {

        return mpaStorage.getMPA();
    }

    public MPA getMPA(Integer id) {

        if (id == null || id < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный запрос или id MPA");
        }
        return mpaStorage.getMPA(id);
    }
}
