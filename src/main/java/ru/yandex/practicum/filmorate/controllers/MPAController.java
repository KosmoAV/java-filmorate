package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPA.MPAService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MPAController {

    private static final Logger log = LoggerFactory.getLogger(MPAController.class);

    private MPAService mpaService;

    @Autowired
    public MPAController(MPAService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<MPA> getMPA() {
        log.info("Получен запрос на получение всех MPA");
        return mpaService.getMPA();
    }

    @GetMapping("/{id}")
    public MPA getMPA(@PathVariable Integer id) {
        log.info("Получен запрос на получение MPA с id = " + id);
        return mpaService.getMPA(id);
    }
}
