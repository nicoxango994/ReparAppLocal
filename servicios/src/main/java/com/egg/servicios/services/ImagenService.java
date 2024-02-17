package com.egg.servicios.services;

import com.egg.servicios.repositories.ImagenRepository;
import com.egg.servicios.entities.Imagen;
import com.egg.servicios.exceptions.MiException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Nico
 */
@Service
public class ImagenService {

    @Autowired
    private ImagenRepository imagenRepository;

    @Transactional
    public Imagen createImagen(MultipartFile archivo) throws MiException {
        
        try {
            if (archivo == null || archivo.isEmpty()) { //(if archivo == null || archivo.isEmpty())

                Imagen imagen = new Imagen();

                // Cargar imagen predeterminada desde la carpeta resources/static/img/
                ClassPathResource defaultImageResource = new ClassPathResource("static/img/default.jpeg");
                byte[] defaultImageBytes = StreamUtils.copyToByteArray(defaultImageResource.getInputStream());

                imagen.setMime("image/jpeg");  // Establecer el tipo MIME de la imagen predeterminada
                imagen.setNombre("default.jpeg");  // Nombre de la imagen predeterminada
                imagen.setContenido(defaultImageBytes);

                return imagenRepository.save(imagen);

            } else if (archivo != null) {

                Imagen imagen = new Imagen();
                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());
                return (Imagen) imagenRepository.save(imagen);
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Imagen> getImagenesAll() {
        return imagenRepository.findAll();
    }

    @Transactional
    public Imagen updateImagen(MultipartFile archivo, String idImagen) throws MiException {
        try {

            if (archivo == null || archivo.isEmpty()) {
                Imagen imagen = imagenRepository.getReferenceById(idImagen);
                return imagenRepository.save(imagen);

            } else if (archivo != null) {

                Imagen imagen = new Imagen();
                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());
                return (Imagen) imagenRepository.save(imagen);
            }
            return null;

        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

}