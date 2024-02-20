package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    ExamenRepository examenRepository;
    @Mock
    PreguntaRepository preguntaRepository;
    @InjectMocks
    ExamenServiceImpl service;

    @Test
    void findExamenByName() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        var nombreExamen = "Matematicas";
        var examen = service.findExamenByName(nombreExamen);
        assertTrue(examen.isPresent());
        assertEquals(nombreExamen, examen.get().getNombre());

    }

    @Test
    void testFindExamenPorNombre(){

        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        var nombreExamen = "Quimica";

        var examen = service.findExamenByNameWithQuestions(nombreExamen);

        assertNotNull(examen);
        assertEquals(2, examen.getId());
        assertEquals(nombreExamen, examen.getNombre());
        assertEquals(3,examen.getPreguntas().size());

        verify(examenRepository, atLeastOnce()).findAll();

        verify(preguntaRepository, atMostOnce()).findPreguntasByExamenId(2L);
    }

    @Test
    void testDoThrow(){
        var examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).savePreguntas(anyList());
        assertThrows(IllegalArgumentException.class, ()->{
            service.save(examen);
        });
    }

    @Test
    void testDoAnswer(){
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        doAnswer(examen -> {
            Long id = examen.getArgument(0);
            return id == 1L ? Datos.PREGUNTAS : Collections.EMPTY_LIST;
        }).when(preguntaRepository).findPreguntasByExamenId(anyLong());

        var examen = "Quimica";
        var examenConPreguntas = service.findExamenByNameWithQuestions(examen);

        assertAll(
                () -> assertTrue(examenConPreguntas.getPreguntas().isEmpty(), "El examen es MAtematicas")
        );
    }

    @Test
    void testSaveExamenConPreguntas(){

        var examenConPreguntas = Datos.EXAMEN;
        examenConPreguntas.setPreguntas(Datos.PREGUNTAS);

        Mockito.when(examenRepository.save(examenConPreguntas)).thenReturn(examenConPreguntas);

        Examen examenGuardado = service.save(examenConPreguntas);

        assertNotNull(examenGuardado);
        assertEquals(examenGuardado.getId(), examenGuardado.getId());
        assertEquals(examenGuardado.getNombre(), examenGuardado.getNombre());
        assertEquals(examenConPreguntas.getPreguntas(), examenGuardado.getPreguntas());
    }

    @Test
    void testSaveExamenSinPreguntas(){

        var examenSinPreguntas = Datos.EXAMEN;

        Mockito.when(examenRepository.save(examenSinPreguntas)).thenReturn(examenSinPreguntas);

        Examen examenGuardado = service.save(examenSinPreguntas);

        assertNotNull(examenGuardado);
        assertEquals(examenSinPreguntas.getId(), examenGuardado.getId());
        assertEquals(examenSinPreguntas.getNombre(), examenGuardado.getNombre());
        assertTrue(examenGuardado.getPreguntas().isEmpty());
    }
}