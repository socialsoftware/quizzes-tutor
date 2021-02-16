package pt.ulisboa.tecnico.socialsoftware.tutor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.QuestionSubmissionApplicationalService;

@Component
public class Mailer {
    private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

    public static String QUIZZES_TUTOR_SUBJECT =  "Quizzes Tutor: ";

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(String from, String to, String subject, String body)  {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        if (!activeProfile.equals("dev") && (to != null && to.trim().length() != 0)) {
            mailSender.send(simpleMailMessage);
        } else if (to == null || to.trim().length() == 0) {
            logger.info("email address was null or empty: {}, {}, {}, {}", from, to, subject, body);
        } else if (activeProfile.equals("dev")) {
            logger.info("email no sent because in dev mode: {}, {}, {}, {}", from, to, subject, body);
        }
    }

}