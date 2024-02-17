package com.egg.servicios.repositories;

import com.egg.servicios.entities.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Martin
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, String> {

    @Query("SELECT c FROM Categoria c WHERE c.id = :id")
    public Categoria getCategoriaById(@Param("id") String id);

    @Query("SELECT c FROM Categoria c WHERE c.alta = true")
    public List<Categoria> findCategoriasByAltaTrue();

    @Query("SELECT c FROM Categoria c WHERE c.alta = false")
    public List<Categoria> findCategoriasByAltaFalse();

    @Query("SELECT c FROM Categoria c " +
            "WHERE c.alta = true " +
            "AND c.nombre LIKE :nombre")
    public List<Categoria> findCategoriasByNombre(@Param("nombre") String nombre);

}
