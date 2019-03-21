package com.solveralynx.logchecker;

import com.solveralynx.logchecker.utilis.MailSender;
import com.solveralynx.logchecker.utilis.PropertiesUtils;
import com.solveralynx.logging.Logger;
import com.solveralynx.logging.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;

public class GemaLogChecker {

    private static Logger logger = LoggerFactory.getLogger("logchecker");
    private static final String LOG_CHECKER_PROPERTIES = "logchecker.properties";
    private Properties properties = PropertiesUtils.getProperties(LOG_CHECKER_PROPERTIES);

    private void findPattern() {
        if (properties == null) {
            System.exit(3);
        }
        final String filePath = properties.getProperty("dirPath");
        String specifiedText = "FullAjaxExceptionHandler";
        try {
            File fileDir = FileUtils.getFile(getLastFile(filePath));
            logger.info("Reading last modified file..................... " + "(" + fileDir + ")");
            Scanner scanner = new Scanner(fileDir);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.contains(specifiedText)) {
                    logger.info(specifiedText + " found in a log:............(" + fileDir + ")");
                    MailSender.sendMail();
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting into the file: " + ex.getMessage());
        }
    }

    private static File getLastFile(String dirPath) {
        File lastFile = null;
        try {
            File directory = new File(dirPath);
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                return null;
            }

            lastFile = files[0];
            for (int i = 1; i < files.length; i++) {
                if (lastFile.lastModified() < files[i].lastModified()) {
                    logger.info("Pulling last modified file from............." + "(" + dirPath + ")");
                    lastFile = files[i];
                }
            }
        } catch (Exception ex) {
            logger.warn("Could not pull last modified file." + ex.getMessage());
        }
        return lastFile;
    }

    public static void main(String[] args) {
        new GemaLogChecker().findPattern();
    }
}
