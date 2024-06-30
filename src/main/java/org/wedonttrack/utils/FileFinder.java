package org.wedonttrack.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The FileFinder class is responsible for finding all mustache files in a specified package.
 * It uses the java.nio.file API to walk the file tree and filter for mustache files.
 */
public class FileFinder {
    private static final Logger LOGGER = LogManager.getLogger(FileFinder.class);

    /**
     * Finds all mustache files in a specified package.
     *
     * @param collectionPackage the package to search in
     * @return a list of mustache files found
     */
    public List<String> findMustacheFiles(String collectionPackage) {
        LOGGER.info("Finding all mustache files in: {}", collectionPackage);
        return findSpecificFiles(collectionPackage, ".mustache");
    }

    public List<String> findCSVFiles(String collectionPackage) {
        LOGGER.info("Finding all csv files in {}", collectionPackage);
        return findSpecificFiles(collectionPackage, ".csv");
    }


    private List<String> findSpecificFiles(String collectionPackage, String fileExtension) {
        LOGGER.info("Finding all files in: {} with extension: {}", collectionPackage, fileExtension);
        List<String> files = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(collectionPackage))) {
            paths.filter(Files::isRegularFile).forEach(filePath -> {
                if (filePath.toString().endsWith(fileExtension)) {
                    LOGGER.info("Found file: {} with extension: {}", filePath.toString(), fileExtension);
                    files.add(filePath.toString());
                }
            });
        } catch (IOException e) {
            LOGGER.error("Unable to open {}", collectionPackage, e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }
}
