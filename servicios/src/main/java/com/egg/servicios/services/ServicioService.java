package com.egg.servicios.services;

import com.egg.servicios.entities.Categoria;
import com.egg.servicios.entities.Imagen;
import com.egg.servicios.entities.Servicio;
import com.egg.servicios.entities.Usuario;
import com.egg.servicios.enums.Ubicacion;
import com.egg.servicios.repositories.CategoriaRepository;
import com.egg.servicios.repositories.ServicioRepository;
import com.egg.servicios.exceptions.MiException;

import com.egg.servicios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Daniel
 */
@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ImagenService imagenService;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public void createServicio(String descripcion, Double honorariosHora, MultipartFile matricula, String idCategoria, String idProveedor) throws MiException {

        validation(descripcion, honorariosHora, matricula, idCategoria, idProveedor);

        try {
            Servicio servicio = new Servicio();

            servicio.setDescripcion(descripcion);

            servicio.setHonorariosPorHora(honorariosHora);

            Imagen imagen = imagenService.createImagen(matricula);
            servicio.setMatricula(imagen);

            Categoria categoria = categoriaRepository.findById(idCategoria).get();
            servicio.setCategoria(categoria);

            Usuario proveedor = usuarioRepository.findById(idProveedor).get();
            servicio.setProveedor(proveedor);

            servicioRepository.save(servicio);

        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }
    
    public Servicio getServicioById(String idServicio) {
        return servicioRepository.getServicioById(idServicio);
    }

    public List<Servicio> getServiciosAll() {
        return servicioRepository.findAll();
    }
    
    public List<Servicio> findServiciosByAltaTrue() {
        return servicioRepository.findServiciosByAltaTrue();
    }

    public List<Servicio> findServiciosDisponibleByIdCliente(String idCliente) {
        return servicioRepository.findServiciosDisponibleByIdCliente(idCliente);
    }

    public List<Servicio> findServiciosByIdProveedor(String idProveedor) {
        return servicioRepository.findServiciosByIdProveedor(idProveedor);
    }

    public List<Servicio> findServiciosByBusqueda(String input) {
        return servicioRepository.findServiciosByBusqueda(input);
    }

    public List<Servicio> findServiciosByBusquedaDisponibleCliente(String idCliente, String input) {
        return servicioRepository.findServiciosByBusquedaDisponibleCliente(idCliente, input);
    }

    public List<Servicio> findServiciosByBusquedaZona(Ubicacion ubicacion) {
        return servicioRepository.findServiciosByBusquedaZona(ubicacion);
    }

    @Transactional
    public void updateServicio(String idServicio, String descripcion, Double honorariosHora, MultipartFile archivo, String idCategoria, String idProveedor) throws MiException {

        validation(descripcion, honorariosHora, archivo, idCategoria, idProveedor);

        try {
            Optional<Servicio> respuesta = servicioRepository.findById(idServicio);

            if (respuesta.isPresent()) {

                Servicio servicio = respuesta.get();

                String idImagen = null;
                if (servicio.getMatricula() != null) {
                    idImagen = servicio.getMatricula().getId();
                }
                Imagen matricula = imagenService.updateImagen(archivo, idImagen);
                servicio.setMatricula(matricula);

                servicio.setDescripcion(descripcion);

                servicio.setHonorariosPorHora(honorariosHora);

                Categoria categoria = categoriaRepository.findById(idCategoria).get();
                servicio.setCategoria(categoria);

                Usuario proveedor = usuarioRepository.findById(idProveedor).get();
                servicio.setProveedor(proveedor);

                servicioRepository.save(servicio);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    @Transactional
    public void updateServicioAltaTrue(String idServicio) throws MiException {
        try {
            Optional<Servicio> respuesta = servicioRepository.findById(idServicio);

            if (respuesta.isPresent()) {

                Servicio servicio = respuesta.get();
                servicio.setAlta(true);

                servicioRepository.save(servicio);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    @Transactional
    public void deleteServicioById(String idServicio) throws MiException {

        try {
            Optional<Servicio> respuesta = servicioRepository.findById(idServicio);

            if (respuesta.isPresent()) {

                Servicio servicio = respuesta.get();
                servicio.setAlta(false);
                servicioRepository.save(servicio);

            } else {
                throw new MiException("El ID Servicio no corresponde a ningun servicio existente.");
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public void validation(String descripcion, Double honorariosHora, MultipartFile archivo, String idCategoria, String idProveedor) throws MiException {

        if (archivo == null || archivo.isEmpty()) {
            throw new MiException("El archivo no puede ser nulo o estar vacio.");
        }
        if (descripcion == null) {
            throw new MiException("La descripcion no puede ser nula.");
        } else if (descripcion.trim().isEmpty()) {
            throw new MiException("La descripcion no puede estar vacia.");
        } else if (descripcion.length() >= 255) {
            throw new MiException("La descripcion ingresada supera la capacidad maxima de 255 caracteres.");
        } else if (existsByDescripcion(descripcion)) {
            throw new MiException("Ya existe un Servicio publicado con la misma descripcion!");
        }
        if (honorariosHora < 1 || honorariosHora.isNaN() || honorariosHora == null) {
            throw new MiException("Los honorarios no deben ser nulos y deben ser un numero valido.");
        }
        if (idCategoria.trim().isEmpty() || idCategoria == null) {
            throw new MiException("El ID Categoria no puede ser nulo o estar vacio.");
        } else if (!categoriaRepository.findById(idCategoria).isPresent()) {
            throw new MiException("El ID Categoria no corresponde a ninguna categoria existente.");
        }
        if (idProveedor.trim().isEmpty() || idProveedor == null) {
            throw new MiException("El ID Proveedor no puede ser nulo o estar vacio.");
        } else if (!usuarioRepository.findById(idProveedor).isPresent()) {
            throw new MiException("El ID Proveedor no corresponde a ningun proveedor existente.");
        }
    }

    public boolean existsByDescripcion(String descripcion) throws MiException {
        try {
            List<Servicio> servicios = servicioRepository.findServiciosByDescripcion(descripcion);
            if (servicios.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

}