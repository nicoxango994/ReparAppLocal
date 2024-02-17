package com.egg.servicios.repositories;

import com.egg.servicios.entities.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nico
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    public Usuario getUsuarioById(@Param("id") String id);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Optional<Usuario> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.accUsuario = :accUsuario")
    public Optional<Usuario> findByAccUsuario(@Param("accUsuario") String accUsuario);

    @Query("SELECT u FROM Usuario u WHERE u.alta = true")
    public List<Usuario> findUsuariosByAltaTrue();

    @Query("SELECT u FROM Usuario u WHERE u.alta = false")
    public List<Usuario> findUsuariosByAltaFalse();

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'CLIENTE' ORDER BY u.nombre ASC")
    public List<Usuario> findUsuariosCliente();

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'PROVEEDOR' ORDER BY u.nombre ASC")
    public List<Usuario> findUsuariosProveedor();

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'ADMIN' ORDER BY u.nombre ASC")
    public List<Usuario> findUsuariosAdmin();

}