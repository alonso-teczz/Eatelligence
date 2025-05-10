package com.alonso.eatelligence.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine emailTemplateEngine;

    /**
     * Envia un correo a la dirección proporcionada con el asunto y el contenido
     * definidos por el template y los datos pasados como par metro.
     *
     * @param to       la dirección del destinatario
     * @param subject  el asunto del correo
     * @param template el nombre del template de Thymeleaf para el correo
     * @param data     los datos para reemplazar en el template
     * @throws MessagingException   si ocurre un error al enviar el correo
     * @throws UnsupportedEncodingException si el nombre del remitente no se puede codificar
     */
    public void sendEmail(String to, String subject, String template, Map<String, Object> data) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariables(data);
        String htmlContent = this.emailTemplateEngine.process(template, context);

        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom(new InternetAddress("eatelligence.app@gmail.com", "Eatelligence"));

        this.mailSender.send(message);
    }
}
