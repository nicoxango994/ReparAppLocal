package com.egg.servicios.repositories;

import com.egg.servicios.entities.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Daniel
 */
@Repository
public interface OfertaRepository extends JpaRepository<Oferta, String> {

    @Query("SELECT o FROM Oferta o WHERE o.id = :id")
    public Oferta getOfertaById(@Param("id") String idOferta);

    @Query("SELECT o FROM Oferta o WHERE o.alta = true")
    public List<Oferta> findOfertasByAltaTrue();

    @Query("SELECT o FROM Oferta o WHERE o.alta = false")
    public List<Oferta> findOfertasByAltaFalse();

    @Query("SELECT o FROM Oferta o WHERE " +
            "o.servicio.id = (SELECT s.id " +
            "FROM Servicio s WHERE " +
            "s.proveedor.id = :idProveedor) ")
    public List<Oferta> findOfertasByIdProveedor(@Param("idProveedor") String idProveedor);

    @Query("SELECT o FROM Oferta o WHERE o.cliente.id = :idCliente")
    public List<Oferta> findOfertasByIdCliente(@Param("idCliente") String idCliente);

    @Query("SELECT o FROM Oferta o WHERE o.descripcion LIKE :descripcion")
    public Optional<Oferta> findByDescripcion(@Param("descripcion") String descripcion);

}