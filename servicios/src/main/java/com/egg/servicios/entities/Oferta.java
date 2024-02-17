package com.egg.servicios.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 *
 * @author Daniel
 */
@Entity
@Table(name = "ofertas")
public class Oferta {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String descripcion;
    private Boolean alta;
    @ManyToOne
    private Servicio servicio;
    @OneToOne
    private Usuario cliente;

    public Oferta() {
        this.alta = true;
    }

    public Oferta(String descripcion, Servicio servicio, Usuario cliente) {
        this.descripcion = descripcion;
        this.servicio = servicio;
        this.cliente = cliente;
        this.alta = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

}