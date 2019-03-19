package com.solveralynx.logchecker;

import com.solveralynx.logging.Logger;
import com.solveralynx.logging.LoggerFactory;
import org.apache.commons.io.FileUtils;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class GemaLogChecker {

    private static final Logger logger = LoggerFactory.getLogger("logchecker");
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
        final String dirPath = properties.getProperty("dirPath");
        String specifiedText = "FullAjaxExceptionHandler";
        try {
            File fileDir = FileUtils.getFile(getLastModified(dirPath));
            logger.info("Reading last modified file..................... " + "(" + fileDir + ")");
            Scanner scanner = new Scanner(fileDir);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(specifiedText)) {
                    logger.error(specifiedText + " found in a log:...................." + fileDir + ".Sending an email...");
                    //sendEmail();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static File getLastModified(String dirPath) {
        File lastModifiedFile = null;
        try {
            File directory = new File(dirPath);
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                return null;
            }

            lastModifiedFile = files[0];
            for (int i = 1; i < files.length; i++) {
                if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                    logger.info("Pulling last modified file from............." + "(" + dirPath + ")");
                    lastModifiedFile = files[i];
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lastModifiedFile;
    }

    public void sendEmail() throws MessagingException {

    }

    public static void main(String[] args) {
        new GemaLogChecker().findPattern();
    }
}
