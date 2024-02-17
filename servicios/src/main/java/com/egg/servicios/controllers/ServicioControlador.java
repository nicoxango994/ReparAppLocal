package com.egg.servicios.controllers;

import com.egg.servicios.entities.*;
import com.egg.servicios.enums.Estados;
import com.egg.servicios.enums.Ubicacion;
import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.repositories.OfertaRepository;

import com.egg.servicios.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.egg.servicios.repositories.ContratoRepository;

/**
 *
 * @author Daniel
 */
@Controller
@RequestMapping("/servicio")
public class ServicioControlador {

    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private ServicioService servicioService;
    @Autowired
    private OfertaService ofertaService;
    @Autowired
    private ContratoService contratoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ContratoRepository contratoRepositorios;
    @Autowired
    private CalificacionService calificacionService;
    @Autowired
    private OfertaRepository ofertaRepository;

    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR', 'ROLE_ADMIN')")
    @GetMapping("/registrar") // localhost:8080/servicio/registrar
    public String registrarServicio(ModelMap modelo, HttpSession session) {

        cargarModeloConCategorias(modelo);

        Usuario proveedor = (Usuario) session.getAttribute("usuarioSession");
        modelo.addAttribute("proveedor", proveedor);
        modelo.put("notificaciones", usuarioService.countNotificaciones(proveedor.getId()));

        return "registrar-servicio.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR','ROLE_ADMIN')")
    @PostMapping("/registro") // localhost:8080/servicio/registro
    public String registroServicio(@RequestParam String descripcion, @RequestParam Double honorariosHora,
            MultipartFile matricula, @RequestParam String idCategoria,
            @RequestParam String idProveedor, ModelMap modelo, HttpSession session) {
        try {

            servicioService.createServicio(descripcion, honorariosHora, matricula, idCategoria, idProveedor);

            cargarModeloConCategorias(modelo);

            Usuario proveedor = (Usuario) session.getAttribute("usuarioSession");
            modelo.addAttribute("proveedor", proveedor);

            modelo.put("exito", "El Servicio fue registrado correctamente!"); // carga el modelo con un mensaje exitoso

            return "redirect:/servicio/registrar";

        } catch (MiException ex) {

            cargarModeloConCategorias(modelo);

            Usuario proveedor = (Usuario) session.getAttribute("usuarioSession");
            modelo.addAttribute("proveedor", proveedor);

            modelo.put("error", ex.getMessage()); // carga el modelo con un mensaje de error

            modelo.put("descripcion", descripcion);
            modelo.put("honorariosHora", honorariosHora);
            modelo.put("matricula", matricula);
            modelo.put("idCategoria", idCategoria);
            modelo.put("idProveedor", idProveedor);

            return "redirect:/servicio/registrar"; // volvemos a cargar el formulario
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE')")
    @PostMapping("/contratar") // localhost:8080/servicio/contratar
    public String registroContrato(@RequestParam String descripcion, @RequestParam String idServicio,
            ModelMap modelo, HttpSession session) {

        try {
            Usuario cliente = (Usuario) session.getAttribute("usuarioSession");
            modelo.addAttribute("cliente", cliente);

            Oferta oferta = new Oferta();
            oferta.setDescripcion(descripcion);
            Servicio servicio = servicioService.getServicioById(idServicio);
            oferta.setServicio(servicio);
            oferta.setCliente(cliente);
            ofertaRepository.save(oferta);

            contratoService.createContrato(oferta.getId());
            return "redirect:/servicio/listar/cliente";

        } catch (MiException ex) {

            modelo.put("error", ex.getMessage());
            return "redirect:/servicio/listar/cliente";
        }

    }

    //AGREGADO 13/12 PARA ADMIN CONTROLADOR
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarservicios")
    public String listarServiciosADM(ModelMap modelo) {
        List<Servicio> servicios = servicioService.getServiciosAll();
        modelo.addAttribute("servicios", servicios);
        return "listar-servicios-adm.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/estados/{id}")
    public String modificarServicio(@PathVariable String id, ModelMap modelo) {

        Servicio servicio = servicioService.getServicioById(id);
        modelo.put("servicio", servicio);

        cargarModeloConCategorias(modelo);

        return "test_servicio_update.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/estados/{id}")
    public String darBaja(@PathVariable String id, ModelMap modelo) {

        try {
            servicioService.deleteServicioById(id);
            
            cargarModeloConCategorias(modelo);

            modelo.put("exito", "Servicio dado de baja!");

            return "test_servicio_read.html";

        } catch (MiException ex) {

            return "test_servicio_update.html";
        }
    }

    @GetMapping("/listar")
    public String listarServicios(ModelMap modelo, HttpSession session) {

        try {
            if (session.getAttribute("usuarioSession") != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
                modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));
            }

            // Se carga la lista de servicios en su totalidad
            List<Servicio> servicios = servicioService.findServiciosByAltaTrue();
            // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
            List<Integer> puntuaciones = cargarListaPuntuacionesServicios(servicios);

            modelo.addAttribute("servicios", servicios);
            modelo.addAttribute("puntuaciones", puntuaciones);

            return "listar-servicios.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios.html";
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_ADMIN')")
    @GetMapping("/listar/cliente")
    public String listarServiciosCliente(ModelMap modelo, HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.put("usuario", usuario);
            modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

            // Se carga la lista de servicios evitando mostrar servicios ya solicitados por el cliente
            List<Servicio> servicios = servicioService.findServiciosDisponibleByIdCliente(usuario.getId());
            // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
            List<Integer> puntuaciones = cargarListaPuntuacionesServicios(servicios);

            modelo.addAttribute("servicios", servicios);
            modelo.addAttribute("puntuaciones", puntuaciones);

            return "listar-servicios-cliente.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios-cliente.html";
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR','ROLE_ADMIN')")
    @GetMapping("/listar/proveedor")
    public String listarServiciosProveedor(ModelMap modelo, HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.put("usuario", usuario);
            modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

            // Se carga la lista de servicios en su totalidad
            List<Servicio> servicios = servicioService.findServiciosByAltaTrue();
            // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
            List<Integer> puntuaciones = cargarListaPuntuacionesServicios(servicios);

            modelo.addAttribute("servicios", servicios);
            modelo.addAttribute("puntuaciones", puntuaciones);

            return "listar-servicios-proveedor.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios-proveedor.html";
        }

    }

    @GetMapping("/listar/buscar")
    public String listarServiciosBuscar(@RequestParam String input, ModelMap modelo, HttpSession session) {

        try {
            if (session.getAttribute("usuarioSession") != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
                modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));
            }

            // Se carga la lista de servicios en su totalidad
            List<Servicio> servicios = servicioService.findServiciosByBusqueda(input);
            // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
            List<Integer> puntuaciones = cargarListaPuntuacionesServicios(servicios);

            modelo.addAttribute("servicios",servicios);
            modelo.addAttribute("puntuaciones",puntuaciones);

            return "listar-servicios.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_ADMIN')")
    @GetMapping("/listar/cliente/buscar")
    public String listarServiciosBuscarCliente(@RequestParam String input, ModelMap modelo, HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.put("usuario", usuario);
            modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

            // Se carga la lista de servicios evitando mostrar servicios ya solicitados por el cliente
            List<Servicio> servicios = servicioService.findServiciosByBusquedaDisponibleCliente(usuario.getId(), input);
            // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
            List<Integer> puntuaciones = cargarListaPuntuacionesServicios(servicios);

            modelo.addAttribute("servicios",servicios);
            modelo.addAttribute("puntuaciones",puntuaciones);

            return "listar-servicios-cliente.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios-cliente.html";
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR','ROLE_ADMIN')")
    @GetMapping("/listar/proveedor/buscar")
    public String listarServiciosBuscarProveedor(@RequestParam String input, ModelMap modelo, HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            modelo.put("usuario", usuario);
            modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));

            // Se carga la lista de servicios en su totalidad
            List<Servicio> servicios = servicioService.findServiciosByBusqueda(input);
            // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
            List<Integer> puntuaciones = cargarListaPuntuacionesServicios(servicios);

            modelo.addAttribute("servicios",servicios);
            modelo.addAttribute("puntuaciones",puntuaciones);

            return "listar-servicios-proveedor.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios-proveedor.html";
        }

    }

    @GetMapping("/listar/zona/buscar")
    public String listarServiciosBuscarZona(@RequestParam("input") Ubicacion ubicacion, ModelMap modelo, HttpSession session) {

        try {
            if (session.getAttribute("usuarioSession") != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
                modelo.put("notificaciones", usuarioService.countNotificaciones(usuario.getId()));
            }

            // Se carga la lista de servicios en su totalidad
            List<Servicio> servicios = servicioService.findServiciosByBusquedaZona(ubicacion);
            // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
            List<Integer> puntuaciones = cargarListaPuntuacionesServicios(servicios);

            modelo.addAttribute("servicios",servicios);
            modelo.addAttribute("puntuaciones",puntuaciones);

            return "listar-servicios.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios.html";
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR', 'ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modificarServicio(@PathVariable String id, ModelMap modelo, HttpSession session) {

        Servicio servicio = servicioService.getServicioById(id);
        modelo.put("servicio", servicio);

        cargarModeloConCategorias(modelo);

        return "test_servicio_update.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR','ROLE_ADMIN')")
    @PostMapping("/modificado/{id}")
    public String modificado(@PathVariable String id, @RequestParam String descripcion,
            @RequestParam Double honorariosHora, MultipartFile matricula,
            @RequestParam String idCategoria, @RequestParam String idProveedor, ModelMap modelo) {

        try {
            servicioService.updateServicio(id, descripcion, honorariosHora, matricula, idCategoria, idProveedor);

            cargarModeloConCategorias(modelo);

            modelo.put("exito", "Servicio actualizado correctamente!");

            return "redirect:/";

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("id", id);
            modelo.put("descripcion", descripcion);
            modelo.put("honorariosHora", honorariosHora);
            modelo.put("matricula", matricula);
            modelo.put("idCategoria", idCategoria);
            modelo.put("idProveedor", idProveedor);

            return "test_servicio_update.html";
        }
    }

    public void cargarModeloConCategorias(ModelMap modelo) {

        try {
            List<Categoria> categorias = categoriaService.findCategoriasByAltaTrue();
            modelo.addAttribute("categorias", categorias);

        } catch (MiException ex) {

            modelo.put("error", ex.getMessage());
        }
    }

    public List<Integer> cargarListaPuntuacionesServicios(List<Servicio> servicios) throws MiException {

        // Se guardara la puntuacion de cada proveedor en orden por cada servicio mostrado
        List<Integer> puntuaciones = new ArrayList<>();

        // Recorre cada servicio mostrado
        for (Servicio servicio : servicios) {

            // Del servicio actual, busca el id del proveedor, y retorna los contratos de ese proveedor
            List<Contrato> contratos = contratoService.findContratosByIdProveedor(servicio.getProveedor().getId());
            int cantEstrellas = 0;
            int cantCalificaciones = 0;

            // Recorre cada contrato del proveedor actual
            for (Contrato contrato : contratos) {

                // Si el contrato fue finalizado y su puntuacion ya esta publicada
                if (contrato.getEstadoTrabajo().equals(Estados.FINALIZADO) && contrato.getAptitud() != null) {
                    // 1 puntuacion mas
                    cantCalificaciones++;
                    // Se acumula la cantidad de estrellas recibidas
                    cantEstrellas += contrato.getAptitud().getPuntuacion();
                }
            }

            // Se realiza un promedio de puntuacion
            int promedioProveedor = cantCalificaciones != 0 ? cantEstrellas / cantCalificaciones : 0;
            // Se añade el promedio del proveedor
            puntuaciones.add(promedioProveedor);
        }

        return puntuaciones;
    }

    @GetMapping("/categorias")
    public String seleccionarServicio(ModelMap modelo, HttpSession session, String nameCate) {
        try {
            modelo.addAttribute("categoria", categoriaService.findCategoriasByAltaTrue());
            return "index.html";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            return "listar-servicios-proveedor.html";
        }

    }

}