package de.lise.terradoc.core.state;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StateReader {

    private final Gson gson;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public StateReader() {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }


    public TerraformState read(InputStream in) throws IOException {
        try(InputStreamReader reader = new InputStreamReader(in, DEFAULT_CHARSET)){
            return read(reader);
        }
    }

    public TerraformState read(Reader reader) {
        return gson.fromJson(reader, TerraformState.class);
    }

}
