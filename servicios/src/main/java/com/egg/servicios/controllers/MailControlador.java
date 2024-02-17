package com.egg.servicios.controllers;

import com.egg.servicios.exceptions.MiException;
import com.egg.servicios.services.MailService;
import com.egg.servicios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Martin
 */
@Controller
@RequestMapping("/email")
public class MailControlador {

    @Autowired
    private MailService mailService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/email")
    public String recuperarPassword() {

        return "test_recuperar_password.html";
    }

    @PostMapping("/enviado")
    public String autenticar(@RequestParam String email) throws MiException {

        String valor = mailService.generarNumeroAleatorio();
        usuarioService.updateUsuarioRecoveryPassword(email, valor);
        mailService.sendMessageUser(email, valor);

        return "login.html";
    }

}
