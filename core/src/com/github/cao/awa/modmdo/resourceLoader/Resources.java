package com.github.cao.awa.modmdo.resourceLoader;

import java.io.*;

public class Resources {
    public static InputStream getResource(String resource, Class<?> getC) {
        InputStream stream = getC.getClassLoader().getResourceAsStream(resource);
        return stream == null ? getC.getResourceAsStream(resource) : stream;
    }

    public static File getResourceByFile(String resource, Class<?> getC) {
        return new File(String.valueOf(getC.getResource(resource)));
    }
}
