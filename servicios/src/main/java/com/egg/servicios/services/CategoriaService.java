package com.egg.servicios.services;

import com.egg.servicios.entities.Categoria;
import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.repositories.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author martin
 */
@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public void createCategoria(String nombre) throws MiException {
        validation(nombre);
        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoriaRepository.save(categoria);
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public Categoria getCategoriaById(String id) {
        return categoriaRepository.getCategoriaById(id);
    }

    public List<Categoria> findCategoriasByAltaTrue() throws MiException {
        try {
            return categoriaRepository.findCategoriasByAltaTrue();
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public List<Categoria> findCategoriasByAltaFalse() throws MiException {
        try {
            return categoriaRepository.findCategoriasByAltaFalse();
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public List<Categoria> findCategoriasByNombre(String nombre) throws MiException {
        try {
            return categoriaRepository.findCategoriasByNombre(nombre);
        } catch (Exception e) {
            throw new MiException("Ya existe una categoría con el mismo nombre");
        }
    }

    @Transactional
    public void updateCategoria(String id, String nombre) throws MiException {
        validation(nombre);
        try {
            Optional<Categoria> resultado = categoriaRepository.findById(id);
            if (resultado.isPresent()) {
                Categoria categoria = resultado.get();
                categoria.setNombre(nombre);
                categoriaRepository.save(categoria);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    @Transactional
    public void updateCategoriaAltaTrue(String id) throws MiException {
        Optional<Categoria> respuesta = categoriaRepository.findById(id);
        try {
            if (respuesta.isPresent()) {
                Categoria categoria = respuesta.get();
                categoria.setAlta(true);
                categoriaRepository.save(categoria);
            } else {
                throw new MiException("El ID categoría no corresponde a ninguna categoría existente.");
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    @Transactional
    public void deleteCategoriaById(String id) throws MiException {
        Optional<Categoria> respuesta = categoriaRepository.findById(id);
        try {
            if (respuesta.isPresent()) {
                Categoria categoria = respuesta.get();
                categoria.setAlta(false);
                categoriaRepository.save(categoria);
            } else {
                throw new MiException("El ID categoría no corresponde a ninguna categoría existente.");
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    private void validation(String nombre) throws MiException {

        if (nombre.trim().isEmpty() || nombre == null) {
            throw new MiException("Debe ingresar un nombre");
        } else if (existsByNombre(nombre)) {
            throw new MiException("Ya existe una categoría con el mismo nombre");
        }
    }

    public boolean existsByNombre(String nombre) throws MiException {
        try {
            List<Categoria> categorias = categoriaRepository.findCategoriasByNombre(nombre);
            if (categorias.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

}
