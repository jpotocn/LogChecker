package com.solveralynx.logchecker.utilis;

import com.solveralynx.logging.Logger;
import com.solveralynx.logging.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by janp on 20.3.2019.
 */
public class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger("logChecker");
    private static String encoding;
    private static final String DEFAULT_ENCODING = "UTF-8";

    public static Properties getProperties(String resourceName) {
        Properties properties = new Properties();
        logger.info("Creating properties from '%s'", resourceName);
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException(String.format("No '%s' file on classpath", resourceName));
            }
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("Could not load '%s' file - %s", resourceName, e.toString());
            return null;
        }
        return properties;
    }
}
