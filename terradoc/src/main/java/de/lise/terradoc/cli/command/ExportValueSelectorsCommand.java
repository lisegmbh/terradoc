package de.lise.terradoc.cli.command;

import com.google.gson.Gson;
import de.lise.terradoc.core.io.ResourceLoader;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@CommandLine.Command(mixinStandardHelpOptions = true, name = "value-selectors",
                     description = "Exports the default value selectors by writing them to STDOUT. You may pipe the output to a file.")
public class ExportValueSelectorsCommand implements Runnable {
    @Override
    public void run() {
        Gson gson = new Gson();
        ResourceLoader resourceLoader = new ResourceLoader(StandardCharsets.UTF_8, gson);

        try {
            String resource = resourceLoader.readResourceAsString(ResourceLoader.DEFAULT_VALUE_SELECTORS_RESOURCE_NAME);
            System.out.println(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
