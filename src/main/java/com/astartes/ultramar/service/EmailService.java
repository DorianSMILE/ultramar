package com.astartes.ultramar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(UUID uuid) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("dorian.sonzogni@smile.fr");
        message.setSubject("Première connexion - Changez votre mot de passe");
        message.setText("Bonjour,\n\nMerci de créer votre mot de passe en cliquant sur le lien suivant :\n" + "http://localhost:4200/admin/firstConnexion?uuid=" + uuid);
        mailSender.send(message);
    }

}
