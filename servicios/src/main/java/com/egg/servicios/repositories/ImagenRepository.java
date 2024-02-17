package com.egg.servicios.repositories;

import com.egg.servicios.entities.Imagen;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nico
 */
@Repository
public interface ImagenRepository extends JpaRepository<Imagen, String>{

}
