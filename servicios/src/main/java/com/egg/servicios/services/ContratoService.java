package com.egg.servicios.services;

import com.egg.servicios.entities.Calificacion;
import com.egg.servicios.entities.Contrato;

import com.egg.servicios.entities.Oferta;
import com.egg.servicios.enums.Estados;

import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.repositories.CalificacionRepository;
import com.egg.servicios.repositories.OfertaRepository;

import java.util.Optional;
import javax.transaction.Transactional;

import com.egg.servicios.repositories.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.egg.servicios.repositories.ContratoRepository;

/**
 *
 * @author joaquin
 */
@Service
public class ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private OfertaRepository ofertaRepository;
    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private OfertaService ofertaService;

    @Transactional
    public void createContrato(String idOferta) throws MiException {
        Optional<Oferta> respuestaOferta = ofertaRepository.findById(idOferta);
        Oferta oferta = new Oferta();

        if (respuestaOferta.isPresent()) {
            oferta = respuestaOferta.get();
        }

        try {
            Contrato contrato = new Contrato();
            contrato.setOferta(oferta);
            contrato.setEstadoTrabajo(Estados.PENDIENTE);
            contrato.isAlta();

            contratoRepository.save(contrato);
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public Contrato getContratoById(String idContrato) {
        return contratoRepository.getContratoById(idContrato);
    }

    public List<Contrato> getContratosAll() {
        List<Contrato> contratos = contratoRepository.findAll();
        return contratos;
    }

    public List<Contrato> findContratosByIdProveedor(String idProveedor) {
            List<Contrato> contratos = contratoRepository.findContratosByIdProveedor(idProveedor);
            return contratos;
    }
    
    public List<Contrato> findContratosByIdCliente(String idCliente) {
        List<Contrato> contratos = contratoRepository.findContratosByIdCliente(idCliente);
        return contratos;
    }

    public List<Contrato> findContratosByAltaTrue() {
        List<Contrato> contratos = contratoRepository.findContratosByAltaTrue();
        return contratos;
    }

    public List<Contrato> findContratosByAltaFalse() {
        List<Contrato> contratos = contratoRepository.findContratosByAltaFalse();
        return contratos;
    }

    public List<Contrato> findContratosPendientes() {
        List<Contrato> contratos = contratoRepository.findContratosByEstadoTrabajoPendiente();
        return contratos;
    }

    public List<Contrato> findContratosRechazados() {
        List<Contrato> contratos = contratoRepository.findContratosByEstadoTrabajoRechazado();
        return contratos;
    }

    public List<Contrato> findContratosAceptados() {
        List<Contrato> contratos = contratoRepository.findContratosByEstadoTrabajoAceptado();
        return contratos;
    }

    public List<Contrato> findContratosFinalizados() {
        List<Contrato> contratos = contratoRepository.findContratosByEstadoTrabajoFinalizado();
        return contratos;
    }

    @Transactional
    public void updateContratoAlta(String idContrato, Boolean alta) {
        Optional<Contrato> respuestaContrato = contratoRepository.findById(idContrato);
        Contrato contrato = new Contrato();

        if (respuestaContrato.isPresent()) {
            contrato = respuestaContrato.get();
        }
        contrato.setAlta(alta);
        contratoRepository.save(contrato);
    }

    @Transactional
    public void updateContratoEstadoTrabajo(String idContrato, Estados estado) {
        Optional<Contrato> respuestaContrato = contratoRepository.findById(idContrato);

        if (respuestaContrato.isPresent()) {
            Contrato contrato = respuestaContrato.get();
            contrato.setEstadoTrabajo(estado);

            contratoRepository.save(contrato);
        }
    }

    @Transactional
    public void updateContratoCalificacion(String idContrato, Calificacion calificacion) throws MiException {

        try {
            Optional<Contrato> respuestaContrato = contratoRepository.findById(idContrato);
            if (respuestaContrato.isPresent()) {
                Contrato contrato = respuestaContrato.get();
                contrato.setAptitud(calificacion);

                contratoRepository.save(contrato);
            }

        } catch (Exception ex) {
            throw new MiException(ex.getMessage());
        }
    }

    @Transactional
    public void deleteContratoById(String idContrato) {
        Optional<Contrato> respuestaContrato = contratoRepository.findById(idContrato);
        Contrato contrato = new Contrato();

        if (respuestaContrato.isPresent()) {
            contrato = respuestaContrato.get();
        }
        contrato.setAlta(false);
        contratoRepository.save(contrato);
    }

    public void validation(Estados estado) throws MiException {
        if (estado.equals(null) || estado == null) {
            throw new MiException("El Estado no puede ser nulo !");
        }
    }

}
