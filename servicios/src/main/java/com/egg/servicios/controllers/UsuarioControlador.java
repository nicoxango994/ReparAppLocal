/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.egg.servicios.controllers;

import com.egg.servicios.entities.Usuario;
import com.egg.servicios.enums.Rol;
import com.egg.servicios.enums.Ubicacion;
import com.egg.servicios.services.UsuarioService;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Nico
 */
@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/activos")
    public String listarActivos(ModelMap modelo) {
        List<Usuario> usuarios = usuarioService.findUsuariosByAltaTrue();
        modelo.addAttribute("usuarios", usuarios);

        return "usuarios_list.html";
    }

    @GetMapping("/inactivos")
    public String listarInactivos(ModelMap modelo) {
        List<Usuario> usuarios = usuarioService.findUsuariosByAltaFalse();
        modelo.addAttribute("usuarios", usuarios);

        return "usuarios_list.html";
    }

    @GetMapping("/clientes")
    public String listarClientes(ModelMap modelo) {
        List<Usuario> usuarios = usuarioService.getUsuariosAll();
        modelo.addAttribute("usuarios", usuarios);

        return "usuarios_list.html";
    }

    @GetMapping("/proveedores")
    public String listarProveedores(ModelMap modelo) {
        List<Usuario> usuarios = usuarioService.findUsuariosProveedor();
        modelo.addAttribute("usuarios", usuarios);

        return "usuarios_list.html";
    }

    @GetMapping("/administradores")
    public String listarAdmin(ModelMap modelo) {
        List<Usuario> usuarios = usuarioService.findUsuariosAdmin();
        modelo.addAttribute("usuarios", usuarios);

        return "usuarios_list.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listacompleta")
    public String listarTodos(ModelMap modelo) {
        List<Usuario> usuarios = usuarioService.getUsuariosAll();
        modelo.addAttribute("usuarios", usuarios);
        return "usuarios_list.html";
    }

    @GetMapping("/modificar/{id}")//viaja el id a traves de path variable para modificar, un fragmento de url donde se encuentra determinado recurso
    public String modificar(@PathVariable String id, ModelMap modelo) {//variable string id es variable de path y viaja en url del GetMapping
        Usuario usuario = usuarioService.getUsuarioById(id);
        modelo.put("usuario", usuario);//llave autor = lleva el valor del  autorServicio lo que nos trae el metodo getOne
        modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

        return "modificar-usuario.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/rol/{id}")
    public String modificarRolAdmin(@PathVariable String id, ModelMap modelo) {
        try {
            usuarioService.updateUsuarioRolAdmin(id);

            return "redirect:../listacompleta";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());

            return "usuario_modificar.html";
        }

    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, Boolean alta, ModelMap modelo) {
        try {
            usuarioService.updateUsuarioAlta(id, alta);

            return "redirect:../listacompleta";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());

            return "modificar-usuario.html";
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_PROVEEDOR','ROLE_ADMIN')")
    @GetMapping("/restablecer")
    public String modificarUsuario(HttpSession session, ModelMap modelo) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.addAttribute("ubicaciones", Ubicacion.values());
            modelo.put("rol", usuario.getRol());
            modelo.put("usuario", usuario);
            modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

            if (usuario != null) {
             return "modificar-usuario.html";
            }
            return "inicio.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "modificar-usuario.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/restablecer/{id}")
    public String modificarUsuario2(@PathVariable String id, ModelMap modelo) {
        modelo.addAttribute("ubicaciones", Ubicacion.values());
        modelo.addAttribute("roles", Rol.values());
        modelo.put("usuario", usuarioService.getUsuarioById(id));//llave autor = lleva el valor del  autorServicio lo que nos trae el metodo getOne
        return "modificar-usuario-adm.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ADMIN','ROLE_PROVEEDOR')")
    @PostMapping("/restablecer/{id}")
    public String modificadoUsuario(@PathVariable String id, MultipartFile archivo,String nombre,String email, String accUsuario,Ubicacion ubicacion,HttpSession session, Rol rol,ModelMap modelo) {
        try {

            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.put("usuario", usuario);
              usuarioService.updateUsuario(archivo, id, rol, ubicacion, nombre, accUsuario, email);
                  if (usuario != null) {
             return "redirect:/inicio";
                  }
               return "index.html";

        } catch (Exception ex) {
            
            modelo.put("nombre", nombre);
            modelo.put("accUsuario", accUsuario);
            modelo.put("email", email);
            modelo.put("ubicacion", ubicacion);

            if (rol.equals(Rol.PROVEEDOR)) {
                modelo.put("rol", Rol.PROVEEDOR);
                modelo.addAttribute("ubicaciones", Ubicacion.values());
                return "modificar-usuario.html";
            } 
            if (rol.equals(Rol.CLIENTE)) {
                modelo.put("rol", Rol.CLIENTE);
                modelo.addAttribute("ubicaciones", Ubicacion.values());
                return "modificar-usuario.html";
            }
           return "modificar-usuario.html";
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_PROVEEDOR','ROLE_ADMIN')")
    @GetMapping("/restablecer/password")
    public String modificarPassword(HttpSession session, ModelMap modelo) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.put("usuario", usuario);
            modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

            if (usuario != null) {
                return "modificar-password.html";
            }
            return "index.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "modificar-password.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ADMIN','ROLE_PROVEEDOR')")
    @PostMapping("/restablecer/password/{id}")
    public String modificadoPassword(@PathVariable String id, String password, String password2, HttpSession session, ModelMap modelo) {
        try {

            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.put("usuario", usuario);
            usuarioService.updateUsuarioPassword(id, password, password2);

            if (usuario != null) {
                return "redirect:/logout";
            }
            return "index.html";

        } catch (Exception ex) {

            modelo.put("error", ex.getMessage());

            return "modificar-password.html";
        }

    }
}
