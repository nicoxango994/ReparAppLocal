package com.egg.servicios.controllers;

import com.egg.servicios.entities.Calificacion;
import com.egg.servicios.entities.Usuario;
import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.services.CalificacionService;
import com.egg.servicios.services.ContratoService;
import java.util.List;

import com.egg.servicios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 *
 * @author daniel
 */
@Controller
@RequestMapping("/calificacion")

public class CalificacionControlador {

    @Autowired
    private CalificacionService calificacionService;
    @Autowired
    private ContratoService contratoService;
    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_ADMIN')")
    @GetMapping("/registrar/{idContrato}")
    public String calificar(@PathVariable String idContrato, @RequestParam String comentario, @RequestParam Integer puntuacion, HttpSession session, ModelMap modelo){
        try {

            if (session.getAttribute("usuarioSession") != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
                modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));
            }

            calificacionService.createCalificacion(comentario, puntuacion);
            modelo.put("exito", "Muchas gracias por calificar!");

            return "redirect:/inicio";

        } catch (MiException e) {
            modelo.put("error", e.getMessage());
            return "registrar-calificacion.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listar")
    public String listarCalificaciones(ModelMap modelo){
        List<Calificacion> calificaciones = calificacionService.getCalificaciones();
        modelo.put("calificaciones", calificaciones);
        return "listar-calificaciones-adm.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/baja/{id}")
    public String baja(@PathVariable String id, ModelMap modelo) {

        try {
            Calificacion calificacion = new Calificacion();
            calificacionService.deleteCalificacion(id);
            modelo.addAttribute("calificacion", calificacion);
            modelo.put("exito", "Calificacion de baja!");
            return "redirect:/calificacion/listar";

        } catch (MiException ex) {

            return "redirect:/calificacion/listar";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/alta/{id}")
    public String alta(@PathVariable String id, ModelMap modelo) {

        try {
            Calificacion calificacion = new Calificacion();
            calificacionService.updateCalificacionAltaTrue(id);
            modelo.addAttribute("calificacion", calificacion);
            modelo.put("exito", "Calificacion de alta!");
            return "redirect:/calificacion/listar";

        } catch (MiException ex) {

            return "redirect:/calificacion/listar";
        }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modificarCalificacion(@PathVariable String id, ModelMap modelo) {
        modelo.put("calificacion", calificacionService.getCalificacionById(id));
        return "modificar-calificacion-adm.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificado/{id}")
    public String modificadoCalificacion(ModelMap modelo,@PathVariable String id, String comentario) {
        try {
            Calificacion calificacion = calificacionService.getCalificacionById(id);
            calificacion.setComentario(comentario);

            calificacionService.updateCalificacion(comentario, id);
            return "redirect:/calificacion/listar";
        } catch (MiException e) {
            modelo.put("calificacion", calificacionService.getCalificacionById(id));
            modelo.put("error", e.getMessage());
            return "modificar-calificacion-adm.html";
        }
    }

}