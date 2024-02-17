package com.egg.servicios.infra;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 *
 * @Martin
 */
@Component
public class MailManager {

    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public MailManager(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(String email, String code) {

        MimeMessage message = javaMailSender.createMimeMessage();
        String contenido = MensajeHTML.TEMPLATE_PRUEBA;

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(email);

            int index = 0;
            for (int i = 0; i < code.length(); i++) {
                contenido = setCodeIntemplate(contenido, index, String.valueOf(code.charAt(i)));
                index++;
            }

            helper.setSubject("Recuperar contraseña");
            helper.setText(contenido, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String setCodeIntemplate(String templateCode, int index, String numbero) {

        return templateCode.replace("{" + index + "}", numbero);
    }

}
