package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.Mpa;
import ru.yandex.practicum.filmorate.exeption.MpaNotExistException;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;


    public Mpa getMpaById(Long mpaId) {
        if (mpaId == null) {
            throw new MpaNotExistException("Рейтинг не может быть путсым");
        }
        return mpaStorage.getMpaById(mpaId);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}
