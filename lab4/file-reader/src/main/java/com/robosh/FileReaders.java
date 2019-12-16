package com.robosh;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link FileReaders} privides an API that allow to read whole file into a {@link String} by file name.
 */
public class FileReaders {

    private FileReaders(){

    }
    /**
     * Returns a {@link String} that contains whole text from the file specified by name.
     *
     * @param fileName a name of a text file
     * @return string that holds whole file content
     */
    public static String readWholeFile(String fileName) {
        Path filePath = createPathFromFileName(fileName);

        try(Stream<String> lines = Files.lines(filePath)) {
            return lines.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new FileReaderException();
        }
    }

    private static Path createPathFromFileName(String fileName) {
        URL fileUrl = FileReaders.class.getClassLoader().getResource(fileName);
        try {
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new FileReaderException();
        }
    }
}
