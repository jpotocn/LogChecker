package com.solveralynx.logchecker.utilis;

import com.solveralynx.logging.Logger;
import com.solveralynx.logging.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by janp on 20.3.2019.
 */
public class MailSender {

    private static Properties properties = PropertiesUtils.getProperties("logchecker.properties");
    private static Logger logger = LoggerFactory.getLogger("logChecker");

    public static void sendMail() {
        MimeMessage message;
        String recipient = properties.getProperty("user.recipient");
        try {
            message = createMessage(recipient);
            logger.info("Start of sending mail about GemaLog error......");
            Transport.send(message);
        } catch (MessagingException ex) {
            logger.error("Sending failed........................." + ex.getMessage());
        }
    }

    private static MimeMessage createMessage(String emailAddress) throws MessagingException {
        if (emailAddress == null) {
            logger.error("Email address must not be null");
        }
        logger.debug("Creating message......");
        String sender = properties.getProperty("user.sender");
        String subject = properties.getProperty("user.subject");
        String importance = properties.getProperty("user.importance");
        String description = properties.getProperty("user.body");
        String footer = properties.getProperty("user.footer");
        MimeMessage message = new MimeMessage(getMailSession());
        message.setFrom(new InternetAddress(sender));
        message.setSubject(subject);
        message.setHeader("Importance", importance);
        message.setText(description + footer);
        message.setRecipients(Message.RecipientType.TO, emailAddress);

        logger.info("Message has been created.....");
        return message;
    }

    private static Session getMailSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", MailSender.properties.getProperty("mail.transport.protocol"));
        properties.setProperty("mail.host", MailSender.properties.getProperty("mail.host"));
        return Session.getDefaultInstance(properties, null);
    }
}
