package com.minsait.services;

import com.minsait.models.Examen;

import java.util.Optional;

public interface ExamenService {

    Optional<Examen> findExamenByName(String name);

    Examen findExamenByNameWithQuestions(String name);

    Examen save(Examen examen);
}
