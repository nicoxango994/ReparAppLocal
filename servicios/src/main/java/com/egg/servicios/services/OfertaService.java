package com.egg.servicios.services;

import com.egg.servicios.entities.*;
import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.repositories.OfertaRepository;
import com.egg.servicios.repositories.ServicioRepository;
import com.egg.servicios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Daniel
 */
@Service
public class OfertaService {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ImagenService imagenService;
    @Autowired
    private ServicioRepository servicioRepository;

    @Transactional
    public Oferta createOferta(String descripcion, String idServicio, String idCliente) throws MiException {

        validation(descripcion, idServicio, idCliente);

        try {

            Oferta oferta = new Oferta();

            oferta.setDescripcion(descripcion);

            Servicio servicio = servicioRepository.findById(idServicio).get();
            oferta.setServicio(servicio);

            Usuario cliente = usuarioRepository.findById(idCliente).get();
            oferta.setCliente(cliente);

            return ofertaRepository.save(oferta);

        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }

    }

    @Transactional
    public void updateOferta(String idOferta, String descripcion, String idServicio, String idCliente) throws MiException {

        validation(descripcion, idServicio, idCliente);

        try {
            Optional<Oferta> respuesta = ofertaRepository.findById(idOferta);

            if(respuesta.isPresent()) {

                Oferta oferta = respuesta.get();

                oferta.setDescripcion(descripcion);

                Servicio servicio = servicioRepository.findById(idServicio).get();
                oferta.setServicio(servicio);

                Usuario cliente = usuarioRepository.findById(idCliente).get();
                oferta.setCliente(cliente);

                ofertaRepository.save(oferta);

            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public Oferta getOfertaById(String idOferta) {
        return ofertaRepository.getOfertaById(idOferta);
    }

    public List<Oferta> findOfertasByAltaTrue() {
        return ofertaRepository.findOfertasByAltaTrue();
    }

    @Transactional
    public void updateOfertaAltaTrue(String idOferta) throws MiException {
        try {
            Optional<Oferta> respuesta = ofertaRepository.findById(idOferta);

            if (respuesta.isPresent()) {

                Oferta oferta = respuesta.get();
                oferta.setAlta(true);

                ofertaRepository.save(oferta);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    @Transactional
    public void deleteOfertaById(String idOferta) throws MiException {

        Optional<Oferta> respuesta = ofertaRepository.findById(idOferta);

        try {
            if (respuesta.isPresent()) {

                Oferta oferta = respuesta.get();
                oferta.setAlta(false);
                ofertaRepository.save(oferta);

            } else {
                throw new MiException("El ID Oferta no corresponde a ninguna oferta existente.");
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public void validation(String descripcion, String idServicio, String idCliente) throws MiException {

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new MiException("La descripcion no puede ser nula o estar vacia.");
        } else if (descripcion.length() >= 255) {
            throw new MiException("La descripcion ingresada supera la capacidad maxima de 255 caracteres.");
        } else if (ofertaRepository.findByDescripcion(descripcion).isPresent()) {
            throw new MiException("Ya existe un servicio publicado con la misma descripcion.");
        }
        if (idServicio == null || idServicio.trim().isEmpty()){
            throw new MiException("El ID Servicio no puede ser nulo o estar vacio.");
        } else if (!servicioRepository.findById(idServicio).isPresent()) {
            throw new MiException("El ID Servicio no corresponde a ningun servicio existente.");
        }
        if (idCliente == null || idCliente.trim().isEmpty()){
            throw new MiException("El ID Cliente no puede ser nulo o estar vacio.");
        } else if (!usuarioRepository.findById(idCliente).isPresent()) {
            throw new MiException("El ID Cliente no corresponde a ningun cliente existente.");
        }
    }

}