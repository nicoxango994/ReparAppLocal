package com.egg.servicios.repositories;

import com.egg.servicios.entities.Contrato;
import java.util.List;

import com.egg.servicios.enums.Estados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nico
 */
@Repository
public interface ContratoRepository extends JpaRepository<Contrato, String> {

    @Query("SELECT c FROM Contrato c WHERE c.id = :id")
    public Contrato getContratoById(@Param("id") String id);

    @Query("SELECT c FROM Contrato c WHERE c.alta = true")
    public List<Contrato> findContratosByAltaTrue();

    @Query("SELECT c FROM Contrato c WHERE c.alta = false")
    public List<Contrato> findContratosByAltaFalse();

    @Query("SELECT c FROM Contrato c WHERE c.alta = true AND c.estadoTrabajo = 'PENDIENTE'")
    public List<Contrato> findContratosByEstadoTrabajoPendiente();

    @Query("SELECT c FROM Contrato c WHERE c.alta = true AND c.estadoTrabajo = 'RECHAZADO'")
    public List<Contrato> findContratosByEstadoTrabajoRechazado();

    @Query("SELECT c FROM Contrato c WHERE c.alta = true AND c.estadoTrabajo = 'ACEPTADO'")
    public List<Contrato> findContratosByEstadoTrabajoAceptado();

    @Query("SELECT c FROM Contrato c WHERE c.alta = true AND c.estadoTrabajo = 'FINALIZADO'")
    public List<Contrato> findContratosByEstadoTrabajoFinalizado();

    @Query("SELECT c FROM Contrato c WHERE c.alta = true AND c.oferta.servicio.proveedor.id = :idProveedor")
    public List<Contrato> findContratosByIdProveedor(@Param("idProveedor") String idProveedor);

    @Query("SELECT c FROM Contrato c WHERE c.alta = true AND c.oferta.cliente.id = :idCliente")
    public List<Contrato> findContratosByIdCliente(@Param("idCliente") String idCliente);

    @Query("SELECT c FROM Contrato c " +
            "WHERE c.alta = true " +
            "  AND c.aptitud IS NULL " +
            "  AND c.estadoTrabajo IN :estados " +
            "  AND (c.oferta.servicio.proveedor.id = :idUsuario OR c.oferta.cliente.id = :idUsuario)")
    public List<Contrato> countNotificacionesByIdUsuario(
            @Param("idUsuario") String idUsuario, @Param("estados") Estados[] estados);

}