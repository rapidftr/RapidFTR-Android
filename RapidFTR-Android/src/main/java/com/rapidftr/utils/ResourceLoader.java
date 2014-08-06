package com.rapidftr.utils;

import com.google.common.io.CharStreams;
import lombok.Cleanup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceLoader {

    public static InputStream loadResourceFromClasspath(String resourceName) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);
    }

    public static String loadResourceAsStringFromClasspath(String resourceName) throws IOException {
        @Cleanup InputStream inputStream = loadResourceFromClasspath(resourceName);
        return CharStreams.toString(new InputStreamReader(inputStream));
    }
}
