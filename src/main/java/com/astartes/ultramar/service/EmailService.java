package com.astartes.ultramar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un e-mail simple.
     *
     * @param subject Objet de l'e-mail.
     * @param text    Contenu de l'e-mail.
     */
    public void sendEmail(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("dorian.sonzogni@smile.fr");
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
