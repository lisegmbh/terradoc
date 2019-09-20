package de.lise.terradoc.core.io;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.NoSuchElementException;

public class ResourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoader.class);
    private final ClassLoader classLoader = getClass().getClassLoader();
    private final Charset defaultCharset;
    private final Gson configJsonParser;

    public static final String DEFAULT_STYLESHEET_RESOURCE_NAME = "style.css";
    public static final String DEFAULT_VALUE_SELECTORS_RESOURCE_NAME = "resource-info-mapping.json";
    public static final String DEFAULT_RESOURCE_CREATORS_RESOURCE_NAME = "resource-creators.json";

    public ResourceLoader(Charset defaultCharset, Gson configJsonParser) {
        this.defaultCharset = defaultCharset;
        this.configJsonParser = configJsonParser;
    }

    public String readResourceAsString(String resourceName) throws IOException {
        try (InputStream in = getResourceStream(resourceName)) {
            return IOUtils.toString(in, defaultCharset);
        }
    }

    @Nonnull
    public InputStream getResourceStream(String resourceName) {
        InputStream in = classLoader.getResourceAsStream(resourceName);
        if (in == null) {
            throw new NoSuchElementException(String.format("The resource '%s' could not be found.", resourceName));
        }
        return in;
    }

    public <T> List<T> readResourceAsJsonList(String resourceName, Class<T> type) throws IOException {
        Type typeOfT = TypeToken.getParameterized(List.class, type).getType();

        try(InputStream in = getResourceStream(resourceName)) {
            try(InputStreamReader reader = new InputStreamReader(in, defaultCharset)) {
                return configJsonParser.fromJson(reader, typeOfT);
            }
        }
    }

    public <T> List<T> readFileAsJsonList(File file, Class<T> type) throws IOException {
        Type typeOfT = TypeToken.getParameterized(List.class, type).getType();

        try(InputStream in = new FileInputStream(file)) {
            try(InputStreamReader reader = new InputStreamReader(in, defaultCharset)) {
                return configJsonParser.fromJson(reader, typeOfT);
            }
        }
    }



}
