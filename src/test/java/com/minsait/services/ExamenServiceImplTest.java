package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void testArgumentCaptor(){
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        var examen = service.findExamenByNameWithQuestions("Quimica");

        verify(preguntaRepository).findPreguntasByExamenId(captor.capture());

        assertEquals(2, captor.getValue());
    }

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
        doAnswer(invocation -> {
            Examen examen = invocation.getArgument(0);
            examen.setId(3L);
            return examen;
        }).when(examenRepository).save(any(Examen.class));

        var examenConId = service.save(examenConPreguntas);

        assertNotNull(examenConId.getId());
        assertEquals(3, examenConId.getId());
        assertEquals("Fisica", examenConId.getNombre());
        verify(examenRepository).save(any());
        verify(preguntaRepository, never()).savePreguntas(anyList());

    }

    @Test
    void testSaveExamenSinPreguntas(){

        var examenSinPreguntas = Datos.EXAMEN;
        examenSinPreguntas.setPreguntas(Datos.PREGUNTAS);
        doAnswer(invocation -> {
            Examen examen1 = invocation.getArgument(0);
            examen1.setId(3L);
            return examen1;
        }).when(examenRepository).save(any(Examen.class));

        var examenConId = service.save(examenSinPreguntas);

        assertNotNull(examenConId.getId());
        assertEquals(3, examenConId.getId());
        assertEquals("Fisica", examenConId.getNombre());
        assertTrue(examenConId.getPreguntas().size() > 0);
        verify(examenRepository).save(any());
        verify(preguntaRepository).savePreguntas(anyList());
    }
}