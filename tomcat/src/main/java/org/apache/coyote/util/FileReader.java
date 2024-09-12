package org.apache.coyote.util;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.exception.CoyoteException;

public class FileReader {

    private static final FileReader INSTANCE = new FileReader();

    private static final String STATIC_DIRECTORY_NAME = "static";

    private FileReader() {
    }

    public static FileReader getInstance() {
        return INSTANCE;
    }

    public String read(String fileName) {
        try {
            URI uri = getClass().getClassLoader().getResource(STATIC_DIRECTORY_NAME + "/" + fileName).toURI();
            Path path = Paths.get(uri);
            return Files.readString(path);
        } catch (NullPointerException e) {
            throw new CoyoteException("파일이 존재하지 않습니다.");
        } catch (Exception e) {
            throw new CoyoteException(e);
        }
    }
}
