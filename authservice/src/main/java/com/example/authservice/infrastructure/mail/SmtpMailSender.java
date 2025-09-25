package com.example.authservice.infrastructure.mail;

import com.example.authservice.application.port.MailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.Instant;

@Component
@Profile("prod")
public class SmtpMailSender implements MailSender {

    private final JavaMailSender javaMailSender;

    public SmtpMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMagicLink(String toEmail, String magicUrl, Instant expiresAt) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = String.format(
                    "<h3>Seu Magic Link</h3>" +
                    "<p>Clique no link abaixo para acessar:</p>" +
                    "<a href=\"%s\">%s</a>" +
                    "<p>O link expira em: %s</p>",
                    magicUrl, magicUrl, expiresAt.toString()
            );

            helper.setText(htmlMsg, true);
            helper.setTo(toEmail);
            helper.setSubject("Login com Magic Link");
            helper.setFrom("no-reply@seusistema.com");

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }
}
