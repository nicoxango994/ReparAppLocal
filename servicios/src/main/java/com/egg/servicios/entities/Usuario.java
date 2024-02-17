package com.egg.servicios.entities;

import com.egg.servicios.enums.Rol;
import com.egg.servicios.enums.Ubicacion;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;


import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Nico
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String accUsuario;
    private String password;
    private String nombre;
    @OneToOne
    private Imagen imagen;
    private String email;
    private Ubicacion ubicacion;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private boolean alta;

    public Usuario() {
        this.alta = true;
    }

    public Usuario(String id, String accUsuario, String password, String nombre, Imagen imagen, String email, Ubicacion ubicacion, Rol rol) {

        this.id = id;
        this.accUsuario = accUsuario;
        this.password = password;
        this.nombre = nombre;
        this.imagen = imagen;
        this.email = email;
        this.ubicacion = ubicacion;
        this.rol = rol;
        this.alta = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccUsuario() {
        return accUsuario;
    }

    public void setAccUsuario(String accUsuario) {
        this.accUsuario = accUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isAlta() {
        return alta;
    }

    public void setAlta(boolean alta) {
        this.alta = alta;
    }

}