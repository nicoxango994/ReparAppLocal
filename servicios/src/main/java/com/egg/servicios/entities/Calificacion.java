package com.egg.servicios.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Jose
 */
@Entity
@Table(name = "calificaciones")
public class Calificacion {
    @Id
    @GeneratedValue (generator= "uuid" )
    @GenericGenerator(name = "uuid", strategy ="uuid2")
    private String id;  
    private String comentario;
    private Integer puntuacion;
    private Boolean alta;

    public Calificacion() {
        this.alta = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

}