package com.solveralynx.logchecker;

import com.solveralynx.logging.Logger;
import com.solveralynx.logging.LoggerFactory;
import org.apache.commons.io.FileUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class GemaLogChecker {

    private static final Logger logger = LoggerFactory.getLogger("gema-logchecker");
    private static final String LOG_CHECKER_PROPERTIES = "logchecker.properties";
    private Properties properties = GemaLogChecker.getProperties(LOG_CHECKER_PROPERTIES);

    private static Properties getProperties(String resource) {
        Properties properties = new Properties();
        logger.info("Creating properties from '%s'", resource);
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IOException(String.format("No '%s' file on classpath", resource));
            }
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("Could not load '%s' file - %s", resource, e.toString());
            return null;
        }

        return properties;
    }

    private void findPattern() {
        if (properties == null) {
            System.exit(3);
        }
        final String filePath = properties.getProperty("filePath");
        String specifiedText = "FullAjaxExceptionHandler";
        try {
            File fileDir = FileUtils.getFile(getLastModified(filePath));
            logger.warn("Checking log: " + fileDir);
            Scanner scanner = new Scanner(fileDir);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(specifiedText)) {
                    logger.warn(specifiedText + " found in a log: " + fileDir + ".Sending an email...");
                    sendMessage();
                    break;
                } else {
                    logger.info(specifiedText + " not found in this line.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static File getLastModified(String dirPath) {
        File lastModifiedFile = null;
        try {
            File dir = new File(dirPath);
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                return null;
            }

            lastModifiedFile = files[0];
            for (int i = 1; i < files.length; i++) {
                if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                    lastModifiedFile = files[i];
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lastModifiedFile;
    }

    private void sendMessage() {
        if (properties == null) {
            System.exit(1);
        }

        final String to = properties.getProperty("emailTo");
        final String from = properties.getProperty("emailFrom");
        final String subject = properties.getProperty("emailSubject");
        final String description = properties.getProperty("emailDescription");
        final String host = properties.getProperty("host");

        Properties properties = new Properties();
        properties.put("mail.smtp.host",host);
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setDescription(description);

            Transport.send(message);
            logger.info("Send message successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GemaLogChecker().findPattern();
    }
}
