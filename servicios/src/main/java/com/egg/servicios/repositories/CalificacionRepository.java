package com.egg.servicios.repositories;

import com.egg.servicios.entities.Calificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jose
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, String> {

    @Query("SELECT c FROM Calificacion c WHERE c.id = :id")
    public Calificacion getCalificacionById(@Param("id") String id);

    @Query("SELECT c FROM Calificacion c")
    public List<Calificacion> getCalificacionesAll();

    @Query("SELECT c FROM Calificacion c WHERE c.alta = true")
    public List<Calificacion> findCalificacionesByAltaTrue();

    @Query("SELECT c FROM Calificacion c WHERE c.alta = false")
    public List<Calificacion> findCalificacionesByAltaFalse();

}