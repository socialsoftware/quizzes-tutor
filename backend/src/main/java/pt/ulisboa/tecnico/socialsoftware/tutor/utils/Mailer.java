package pt.ulisboa.tecnico.socialsoftware.tutor.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Mailer {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(String from, String to, String subject, String body){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        if (!activeProfile.equals("dev")) {
            mailSender.send(simpleMailMessage);
        }
    }

}