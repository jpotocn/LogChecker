package com.solveralynx.logchecker;

import com.solveralynx.logging.Logger;
import com.solveralynx.logging.LoggerFactory;
import sun.plugin2.message.transport.Transport;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GemaLogChecker {

    private static final Logger logger = LoggerFactory.getLogger("gema-logchecker");
    private static final String LOG_CHECKER_PROPERTIES = "logchecker.properties";
    private String exception = "FullAjaxExcepionHandler";

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

    private void checkFile() {

        Properties properties = GemaLogChecker.getProperties(LOG_CHECKER_PROPERTIES);
        if (properties == null) {
            System.exit(3);
        }

        final String userEmailFrom = properties.getProperty("emailTo");
        final String filePath = properties.getProperty("filePath");
        final String usernameEmailTo = properties.getProperty("filePath");
        final String host = properties.getProperty("host");
        logger.info("Checking a file!");
        try {
            File file = new File(usernameEmailTo);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
                // Print the content on the console
                System.out.println(text);
                //if(text = reader.readLine().startsWith(exception)){

                Session session = Session.getDefaultInstance("mail.smtp.host",host);


                    MimeMessage message = new MimeMessage(session);


                    Transport.send(message);
                    System.out.println("Sent message successfully....");

            }
            System.out.println("file:");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GemaLogChecker().checkFile();
    }
}
