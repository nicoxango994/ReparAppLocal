package com.egg.servicios.services;

import com.egg.servicios.entities.Calificacion;
import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.repositories.CalificacionRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jose
 */
@Service
public class CalificacionService {

    @Autowired
    CalificacionRepository calificacionRepository;

    @Transactional
    public Calificacion createCalificacion(String comentario, Integer puntuacion) throws MiException {
        validation(comentario, puntuacion);
        try {
            Calificacion calificacion = new Calificacion();

            calificacion.setPuntuacion(puntuacion);
            calificacion.setComentario(comentario);

            return calificacionRepository.save(calificacion);
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public List<Calificacion> getCalificaciones() {
        List<Calificacion> calificaciones = calificacionRepository.getCalificacionesAll();
        return calificaciones;
    }

    public Calificacion getCalificacionById(String id) {
        return calificacionRepository.getById(id);
    }

    @Transactional
    public void updateCalificacion(String comentario, String id) throws MiException {

        try {
            Optional<Calificacion> respuesta = calificacionRepository.findById(id);
            validation(comentario, respuesta.get().getPuntuacion());

            if (respuesta.isPresent()) {
                Calificacion calificacion = respuesta.get();
                calificacion.setComentario(comentario);
                calificacionRepository.save(calificacion);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }

    }

    @Transactional
    public void updateCalificacion(String comentario, Integer puntuacion, String id) throws MiException {

        try {
            validation(comentario, puntuacion);
            Optional<Calificacion> respuesta = calificacionRepository.findById(id);

            if (respuesta.isPresent()) {
                Calificacion calificacion = respuesta.get();
                calificacion.setPuntuacion(puntuacion);
                calificacion.setComentario(comentario);
                calificacionRepository.save(calificacion);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }

    }

    @Transactional
    public void updateCalificacionAltaTrue(String id) throws MiException {

        try {
            Optional<Calificacion> respuesta = calificacionRepository.findById(id);

            if (respuesta.isPresent()) {
                Calificacion calificacion = respuesta.get();
                calificacion.setAlta(true);

                calificacionRepository.save(calificacion);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }

    }

    @Transactional
    public void deleteCalificacion(String id) throws MiException {

        try {
            Optional<Calificacion> respuesta = calificacionRepository.findById(id);

            if (respuesta.isPresent()) {
                Calificacion calificacion = respuesta.get();
                calificacion.setAlta(false); // Eliminacion logica

                calificacionRepository.save(calificacion);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }

    }

    private void validation(String comentario, Integer puntuacion) throws MiException {

        if (puntuacion == null) {
            throw new MiException("La puntuacion no puede ser nula");
        } else if (puntuacion > 5 || puntuacion < 1) {
            throw new MiException("La puntuacion no esta en los parametros");
        }
        if (comentario.length() >= 255) {
            throw new MiException("El comentario ingresado supera la capacidad maxima de 255 caracteres.");
        }
    }
}
