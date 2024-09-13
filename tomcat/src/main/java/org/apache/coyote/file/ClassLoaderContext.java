package org.apache.coyote.file;

import java.io.InputStream;

public class ClassLoaderContext {
    private ClassLoaderContext() {}

    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

    public static InputStream getResourceAsStream(final String name) {
        return CLASS_LOADER.getResourceAsStream(name);
    }
}