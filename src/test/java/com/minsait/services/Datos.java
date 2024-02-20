package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntaRepository;

import java.util.List;

public class Datos {

    public static final List<Examen> EXAMENES = List.of(
            new Examen(1L, "Matematicas"),
            new Examen(2L, "Quimica"),
            new Examen(3L, "Historia")
    );

    public static final Examen EXAMEN = new Examen(null, "Fisica");

    public static final List<String> PREGUNTAS = List.of
            ("aritmética", "geometria","trigonometría");
}
