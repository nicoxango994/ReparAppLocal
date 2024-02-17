package com.egg.servicios.repositories;

import com.egg.servicios.entities.Servicio;

import com.egg.servicios.enums.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Daniel
 */
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, String> {

    @Query("SELECT s FROM Servicio s WHERE s.id = :id ")
    public Servicio getServicioById(@Param("id") String id);

    @Query("SELECT s FROM Servicio s WHERE s.alta = true ")
    public List<Servicio> findServiciosByAltaTrue();

    @Query("SELECT s FROM Servicio s WHERE s.alta = false")
    public List<Servicio> findServiciosByAltaFalse();

    @Query("SELECT s FROM Servicio s WHERE s.alta = true AND s.descripcion LIKE :descripcion")
    public List<Servicio> findServiciosByDescripcion(@Param("descripcion") String descripcion);

    // Selecciona los servicios que estan asociados al proveedor proporcionado
    @Query("SELECT s FROM Servicio s WHERE s.alta = true AND s.proveedor.id = :idProveedor")
    public List<Servicio> findServiciosByIdProveedor(@Param("idProveedor") String idProveedor);

    // Selecciona los servicios activos que no tienen un contrato asociado al cliente proporcionado
    @Query("SELECT s FROM Servicio s " +
            "WHERE s.alta = true " +
            "AND s.id NOT IN (" +
            "SELECT c.oferta.servicio.id " +
            "FROM Contrato c " +
            "WHERE c.oferta.cliente.id = :idCliente " +
            "AND (c.estadoTrabajo = 'PENDIENTE' OR c.estadoTrabajo = 'ACEPTADO'))")
    public List<Servicio> findServiciosDisponibleByIdCliente(@Param("idCliente") String idCliente);

    // Selecciona servicios relacionados según la lupa de búsqueda
    @Query("SELECT s FROM Servicio s " +
            "WHERE s.alta = true " +
            "AND (s.categoria.nombre LIKE %:input% " +
            "OR s.proveedor.nombre LIKE %:input% " +
            "OR s.descripcion LIKE %:input%)")
    public List<Servicio> findServiciosByBusqueda(@Param("input") String input);

    // Selecciona servicios relacionados según la lupa de búsqueda y que el cliente NO HAYA SOLICITADO
    @Query("SELECT s FROM Servicio s WHERE s.alta = true " +
            "AND s.id NOT IN (" +
            "SELECT c.oferta.servicio.id " +
            "FROM Contrato c " +
            "WHERE c.oferta.cliente.id = :idCliente) " +
            "AND (s.categoria.nombre LIKE %:input% " +
            "OR s.proveedor.nombre LIKE %:input% " +
            "OR s.proveedor.ubicacion LIKE %:input% " +
            "OR s.descripcion LIKE %:input%)")
    public List<Servicio> findServiciosByBusquedaDisponibleCliente(@Param("idCliente") String idCliente, @Param("input") String input);

    @Query("SELECT s FROM Servicio s WHERE s.alta = true " +
            "AND s.proveedor.ubicacion = :input")
    public List<Servicio> findServiciosByBusquedaZona(@Param("input") Ubicacion ubicacion);

}
