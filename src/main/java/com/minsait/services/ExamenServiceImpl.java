package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{

    ExamenRepository examenRepository;

    PreguntaRepository preguntaRepository;

    @Override
    public Optional<Examen> findExamenByName(String name) {
        return examenRepository.findAll().stream()
                .filter(examen -> examen.getNombre().equals(name))
                .findFirst();
    }

    @Override
    public Examen findExamenByNameWithQuestions(String name) {
        var examenOptional = findExamenByName(name);
        List<String> preguntas;
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.get();
            preguntas = preguntaRepository.findPreguntasByExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen save(Examen examen) {

        Examen examenGuardado = examenRepository.save(examen);
        if (examen.getPreguntas() != null && !examen.getPreguntas().isEmpty()){
            preguntaRepository.savePreguntas(examen.getPreguntas());
        }
        return examenGuardado;
    }
}

