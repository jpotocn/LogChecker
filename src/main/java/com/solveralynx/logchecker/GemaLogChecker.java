package com.solveralynx.logchecker;

import com.solveralynx.logging.Logger;
import com.solveralynx.logging.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GemaLogChecker {

    private static final Logger logger = LoggerFactory.getLogger("gema-logchecker");
    private static final String LOG_CHECKER_PROPERTIES = "logchecker.properties";

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
        logger.info("Checking a file!");

        Properties properties = GemaLogChecker.getProperties(LOG_CHECKER_PROPERTIES);
        if (properties == null) {
            System.exit(3);
        }

        final String userEmail = properties.getProperty("email");
        final String filePath = properties.getProperty("filePath");

        FileUtils fileUtils = new FileUtils();
    }

    public static void main(String[] args) {
        new GemaLogChecker().checkFile();
    }
}
