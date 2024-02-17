package com.egg.servicios.controllers;

import com.egg.servicios.entities.Calificacion;
import com.egg.servicios.entities.Contrato;
import com.egg.servicios.entities.Usuario;
import com.egg.servicios.enums.Estados;
import com.egg.servicios.enums.Rol;
import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.services.CalificacionService;
import com.egg.servicios.services.OfertaService;
import com.egg.servicios.services.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.egg.servicios.services.ContratoService;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

/**
 *
 * @author Nico
 */
@Controller
@RequestMapping("/contrato")
public class ContratoControlador {

    @Autowired
    private ContratoService contratoService;
    @Autowired
    private OfertaService ofertaService;
    @Autowired
    private CalificacionService calificacionService;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public String crearContrato(ModelMap modelo, @PathVariable String idOferta) throws MiException {

        try {
            contratoService.createContrato(idOferta);
            
            modelo.put("exito", "El contrato se creo correctamente!");

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios-cliente.html";
        }
        return "listar-servicios-cliente.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_PROVEEDOR','ROLE_ADMIN')")
    @GetMapping("/listar")
    public String listarContratos(ModelMap modelo, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        List<Contrato> contratos = new ArrayList<>();
        modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

        if (usuario.getRol().equals(Rol.CLIENTE)) {
            contratos = contratoService.findContratosByIdCliente(usuario.getId());
        } else if (usuario.getRol().equals(Rol.PROVEEDOR)) {
            contratos = contratoService.findContratosByIdProveedor(usuario.getId());
        }

        modelo.addAttribute("contratos", contratos);
        return "listar-contratos.html";
    }

    @GetMapping("/pendientes")
    public String listarPendientes(ModelMap modelo) {
        List<Contrato> contratos = contratoService.getContratosAll();
        modelo.addAttribute("lista", contratos);
        return "listar-contratos.html";
    }

    @GetMapping("/rechazados")
    public String listarRechazados(ModelMap modelo) {
        List<Contrato> contratos = contratoService.getContratosAll();
        modelo.addAttribute("lista", contratos);
        return "listar-contratos.html";
    }

    @GetMapping("/aceptados")
    public String listarAceptados(ModelMap modelo) {
        List<Contrato> contratos = contratoService.getContratosAll();
        modelo.addAttribute("lista", contratos);
        return "listar-contratos.html";
    }

    @GetMapping("/finalizados")
    public String listarFinalizados(ModelMap modelo) {
        List<Contrato> contratos = contratoService.findContratosFinalizados();
        modelo.addAttribute("lista", contratos);
        return "listar-contratos.html";
    }

    @GetMapping("/estados/{id}")
    public String modificar(@PathVariable String id, ModelMap modelo) {
        modelo.put("contrato", contratoService.getContratoById(id));
        return "HTML";
    }

    @PostMapping("/estados/{id}")
    public String modificar(@PathVariable String id, Estados estado, ModelMap modelo) throws MiException {

        try {
            contratoService.updateContratoEstadoTrabajo(id, estado);
            modelo.put("exito", "El Contrato fue modificado correctamente!");
            return "HTML";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "test_contrato_registrar";
        }
    }

    @PostMapping("/aceptar/{id}")
    public String aceptarContrato(ModelMap modelo, @PathVariable String id) throws MiException {
        try {
            contratoService.updateContratoEstadoTrabajo(id, Estados.ACEPTADO);
            modelo.put("exito", "El Contrato fue aceptado correctamente!");
            return "redirect:../listar";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../listar";
        }
    }

    @PostMapping("/rechazar/{id}")
    public String rechazarContrato(ModelMap modelo, @PathVariable String id) throws MiException {
        try {
            contratoService.updateContratoEstadoTrabajo(id, Estados.RECHAZADO);
            modelo.put("exito", "El Contrato fue rechazado correctamente!");
            return "redirect:../listar";
        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../listar";
        }
    }

    @PostMapping("/finalizar/{id}")
    public String finalizarContrato(ModelMap modelo, @PathVariable String id) throws MiException {
        try {
            contratoService.updateContratoEstadoTrabajo(id, Estados.FINALIZADO);
            modelo.put("exito", "El Contrato fue finalizado correctamente!");
            return "redirect:../listar";
        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../listar";
        }
    }

    /*th:href="@{/contrato/listar/cliente}"*/
    @GetMapping("/calificar/{id}")
    public String calificarContrato(@PathVariable String id, ModelMap modelo) {

        modelo.put("contrato", contratoService.getContratoById(id));
        return "registrar-calificacion.html";
    }

    @PostMapping("/calificado/{id}")
    public String calificadoContrato(@PathVariable String id, @RequestParam Integer calificacion,
                                     @RequestParam String comentario, ModelMap modelo) throws MiException {

        try {
            Contrato contrato = contratoService.getContratoById(id);
            modelo.put("contrato", contrato);

            Integer puntaje = (calificacion != null) ? calificacion : 0;

            Calificacion c = calificacionService.createCalificacion(comentario,puntaje);
            contratoService.updateContratoCalificacion(contrato.getId(),c);

            return "redirect:../listar";

        } catch (Exception ex) {
            throw new MiException(ex.getMessage());
        }
    }

    @PostMapping("/eliminar/{id}")
    public String EliminarContrato(@PathVariable String idContrato, ModelMap modelo) throws MiException {

        try {
            contratoService.updateContratoEstadoTrabajo(idContrato, Estados.RECHAZADO);
            modelo.put("exito", "El Contrato fue modificado correctamente!");
            return "HTML";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "test_contrato_registrar";
        }
    }

}
