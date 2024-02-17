package com.egg.servicios.services;

import com.egg.servicios.entities.Contrato;
import com.egg.servicios.entities.Imagen;
import com.egg.servicios.entities.Usuario;
import com.egg.servicios.enums.Estados;
import com.egg.servicios.enums.Rol;
import com.egg.servicios.enums.Ubicacion;
import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.repositories.ContratoRepository;
import com.egg.servicios.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Nico
 */
@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ImagenService imagenService;
    @Autowired
    private ContratoRepository contratoRepository;

    @Transactional
    public void createUsuario(MultipartFile archivo, String accUsuario, Rol rol, String nombre, String email, Ubicacion ubicacion, String password, String password2) throws MiException {

        validation(nombre, email, accUsuario, ubicacion, password, password2);

        Usuario usuario = new Usuario();

        usuario.setAccUsuario(accUsuario);
        usuario.setNombre(nombre);
        usuario.setEmail(email);

        usuario.setUbicacion(ubicacion);

        usuario.setPassword(new BCryptPasswordEncoder().encode(password));

        usuario.setRol(rol);

        Imagen imagen = imagenService.createImagen(archivo);

        usuario.setImagen(imagen);

        usuarioRepository.save(usuario);
    }

    public Usuario getUsuarioById(String id) {
        return usuarioRepository.getUsuarioById(id);
    }

    public List<Usuario> getUsuariosAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios;
    }

    public Usuario findUsuarioByEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            return usuario.get();
        } else {
            return null;
        }
    }

    public List<Usuario> findUsuariosByAltaTrue() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosByAltaTrue();
        return usuarios;
    }

    public List<Usuario> findUsuariosByAltaFalse() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosByAltaFalse();
        return usuarios;
    }

    public List<Usuario> findUsuariosCliente() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosCliente();
        return usuarios;
    }

    public List<Usuario> findUsuariosProveedor() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosProveedor();
        return usuarios;
    }

    public List<Usuario> findUsuariosAdmin() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosAdmin();
        return usuarios;
    }

    @Transactional
    public void updateUsuario(MultipartFile archivo, String idUsuario, Rol rol, Ubicacion ubicacion, String nombre, String accUsuario, String email) throws MiException {

        Optional<Usuario> respuesta = usuarioRepository.findById(idUsuario);

        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();

            validationUpdate(usuario, nombre, email, accUsuario, ubicacion);

            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setUbicacion(ubicacion);

            usuario.setRol(rol);

            String idImagen = null;

            if (usuario.getImagen() != null) {
                idImagen = usuario.getImagen().getId();
            }

            Imagen imagen = (Imagen) imagenService.updateImagen(archivo, idImagen);

            usuario.setImagen(imagen);

            usuarioRepository.save(usuario);
        }

    }

    @Transactional
    public void updateUsuarioPassword(String idUsuario, String password, String password2) throws MiException {
        try {
            validationPassword(password, password2);

            Optional<Usuario> respuesta = usuarioRepository.findById(idUsuario);

            if (respuesta.isPresent()) {
                Usuario usuario = respuesta.get();
                usuario.setPassword(new BCryptPasswordEncoder().encode(password));

                usuarioRepository.save(usuario);

            } else {
                throw new MiException("El usuario con ID " + idUsuario + " no fue encontrado.");
            }
        } catch (Exception ex) {
            throw new MiException(ex.getMessage());
        }
    }

    @Transactional
    public void updateUsuarioRecoveryPassword (String email, String password) throws MiException {
        try {
            Optional<Usuario> respuesta = usuarioRepository.findByEmail(email);

            if (respuesta.isPresent()) {

                Usuario usuario = respuesta.get();
                usuario.setEmail(email);

                usuario.setPassword(new BCryptPasswordEncoder().encode(password));

                usuarioRepository.save(usuario);
            }
        } catch (Exception ex) {
            throw new MiException(ex.getMessage());
        }
    }

    @Transactional
    public void updateUsuarioAltaTrue (String id) {
        Optional<Usuario> usuarioRespuesta = usuarioRepository.findById(id);
        // Persistimos con repositorio, buscamos por id, verificamos que la respuesta este presente y la asignamos a una variable usuario,
        // en esta se setea el alta como falso("eliminado") y se vuelve a persistir para guardar en el repositorio.
        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();
            usuario.setAlta(true);

            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void updateUsuarioAlta (String id, Boolean alta) {
        Optional<Usuario> usuarioRespuesta = usuarioRepository.findById(id);
        //Persistimos con repositorio, buscamos por id, verificamos que la respuesta este presente y la asignamos a una variable usuario,
        // en esta se setea el alta como falso("eliminado") y se vuelve a persistir para guardar en el repositorio.
        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();
            usuario.setAlta(alta);

            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void updateUsuarioRolAdmin(String id) {
        Optional<Usuario> usuarioRespuesta = usuarioRepository.findById(id);
        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();
            usuario.setRol(Rol.ADMIN);

            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void updateUsuarioRolCliente(String id) {
        Optional<Usuario> usuarioRespuesta = usuarioRepository.findById(id);
        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();
            usuario.setRol(Rol.CLIENTE);

            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void updateUsuarioRolProveedor(String id) {
        Optional<Usuario> usuarioRespuesta = usuarioRepository.findById(id);
        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();
            usuario.setRol(Rol.PROVEEDOR);

            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void deleteUsuarioById (String id) {
        Optional<Usuario> usuarioRespuesta = usuarioRepository.findById(id);
        //Persistimos con repositorio, buscamos por id, verificamos que la respuesta este presente y la asignamos a una variable usuario,
        // en esta se setea el alta como falso("eliminado") y se vuelve a persistir para guardar en el repositorio.
        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();
            usuario.setAlta(false);

            usuarioRepository.save(usuario);
        }
    }

    private void validation (String nombre, String email, String accUsuario, Ubicacion ubicacion, String password, String password2) throws MiException {

        // byte bites = (byte) 1048576;

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new MiException("El Nombre no puede ser nulo o estar vacío");
        }
        if (accUsuario == null || accUsuario.trim().isEmpty()) {
            throw new MiException("El Nombre de usuario no puede ser nulo o estar vacio");
        } else if (existsByAccUsuario(accUsuario)) {
            throw new MiException("Ya existe una cuenta con ese Nombre de usuario registrado..");
        }
        if (ubicacion == null || ubicacion.equals("")) {
            throw new MiException("La Ubicacion no puede ser nula o estar vacia");
        }
        if (email == null || email.isEmpty()) {
            throw new MiException("El Email no puede ser nulo o estar vacio");
        } else if (existsByEmail(email)) {
            throw new MiException("Ya existe una cuenta con ese Email registrado..");
        }
        if (password == null || password.isEmpty() || password.length() <= 5) {
            throw new MiException("La Contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las Contraseñas ingresadas deben ser iguales");
        }

    }

    private void validationUpdate (Usuario usuario, String nombre, String email, String accUsuario, Ubicacion ubicacion) throws MiException {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new MiException("El Nombre no puede ser nulo o estar vacío");
        }
        if (accUsuario == null || accUsuario.trim().isEmpty()) {
            throw new MiException("El Nombre de usuario no puede ser nulo o estar vacio");
        } else if (!accUsuario.equals(usuario.getAccUsuario()) && existsByAccUsuario(accUsuario)) {
            throw new MiException("Ya existe una cuenta con ese Nombre de usuario registrado..");
        }
        if (ubicacion == null || ubicacion.equals("")) {
            throw new MiException("La Ubicacion no puede ser nula o estar vacia");
        }
        if (email == null || email.isEmpty()) {
            throw new MiException("El Email no puede ser nulo o estar vacio");
        } else if (!email.equals(usuario.getEmail()) && existsByEmail(email)) {
            throw new MiException("Ya existe una cuenta con ese Email registrado..");
        }

    }

    private void validationPassword (String password, String password2) throws MiException {

        if (password == null || password.isEmpty() || password.length() <= 5) {
            throw new MiException("La Contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las Contraseñas ingresadas deben ser iguales");
        }

    }

    public boolean existsByAccUsuario(String accUsuario) throws MiException {
        try {
            Optional<Usuario> usuario = usuarioRepository.findByAccUsuario(accUsuario);
            if (usuario.isPresent()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    public boolean existsByEmail(String email) throws MiException {
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            if (usuario.isPresent()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new MiException(e.getMessage());
        }
    }

    @Transactional
    public Integer countNotificaciones (String id) {

        Optional<Usuario> usuarioRespuesta = usuarioRepository.findById(id);

        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();

            Estados[] estadosAnalizar = {Estados.PENDIENTE, Estados.ACEPTADO, Estados.FINALIZADO};
            List<Contrato> contratosNotif = contratoRepository.countNotificacionesByIdUsuario(usuario.getId(), estadosAnalizar);

            return  contratosNotif.size();

        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> user = usuarioRepository.findByEmail(email);

        if (user.isPresent()) {
            Usuario usuario = user.get();

            // Si el usuario tiene el alta en 'false'
            if (!usuario.isAlta()) {
                throw new UsernameNotFoundException("La cuenta ha sido baneada o eliminada: " + email);
            }

            List<GrantedAuthority> permisos = new ArrayList<>();
            // Se añaden los permisos
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);

            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }
    }

}