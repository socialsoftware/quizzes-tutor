package pt.ulisboa.tecnico.socialsoftware.tutor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Mailer {
    private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

    public static final String QUIZZES_TUTOR_SUBJECT =  "Quizzes Tutor: ";

    @Value("${spring.mail.host}")
    private String host;

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(String from, String to, String subject, String body)  {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        if (!host.equals("fake-host") && (to != null && to.trim().length() != 0)) {
            mailSender.send(simpleMailMessage);
        } else if (to == null || to.trim().length() == 0) {
            logger.info("email address was null or empty: {}, {}, {}, {}", from, to, subject, body);
        } else if (host.equals("fake-host")) {
            logger.info("email not send due to fake host configuration: {}, {}, {}, {}", from, to, subject, body);
        }
    }

}