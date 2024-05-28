package com.gabriel.projetoestacio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

	public void sendValidationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Validação de E-mail - Livraria Online");
        message.setText("Para validar seu e-mail, utilize o seguinte token:\n\nToken de validação: " + token + "\n\nPor favor, use este token para validar seu e-mail. Se você não solicitou este cadastro, pode ignorar este e-mail com segurança.");
        emailSender.send(message);
    }
}
