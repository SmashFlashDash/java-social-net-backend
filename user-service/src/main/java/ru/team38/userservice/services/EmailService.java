package ru.team38.userservice.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.team38.userservice.exceptions.EmailAuthorizationException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    private final JavaMailSender javaMailSender;

    private void sendEmail(String to, String subject, String messageText, boolean isHtmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(messageText, isHtmlContent);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(emailFrom);
            javaMailSender.send(mimeMessage);
        } catch (Exception ex) {
            throw new EmailAuthorizationException("Failed to send email");
        }
    }

    public void sendPasswordResetEmail(String emailTo, String resetUrl) {
        String htmlMsg = "<div style=\"background-color: #004d40; padding: 10px; color: white; width: 600px; margin: 0 auto;\">" +
                "<h1 style=\"font-size: 24px;\">Password Reset</h1>" +
                "<p style=\"font-size: 18px;\">Please click the below link to reset your password:</p>" +
                "<a href=\"" + resetUrl + "\" style=\"display: inline-block; margin-top: 10px; background-color: #00897b; color: white; padding: 10px; text-decoration: none;\">Reset Password</a>" +
                "</div>";
        sendEmail(emailTo, "Password reset", htmlMsg, true);
    }
}
