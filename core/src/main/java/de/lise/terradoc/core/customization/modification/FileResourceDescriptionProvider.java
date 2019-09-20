package de.lise.terradoc.core.customization.modification;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class FileResourceDescriptionProvider implements ResourceDescriptionProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceDescriptionProvider.class);
    private final File base;

    public FileResourceDescriptionProvider(File base) {
        this.base = base;
    }

    @Override
    public Optional<String> getAsciidocDescription(String fullQualifiedTerraformAddress) {
        File descriptionFile = new File(base, String.format("%s.adoc", fullQualifiedTerraformAddress)).getAbsoluteFile();
        LOGGER.debug("Looking for additional description file at '{}'", descriptionFile.getAbsolutePath());
        if (!descriptionFile.exists() || !descriptionFile.isFile()) {
            LOGGER.debug("Additional description file for resource '{}' could not be found at: {}", fullQualifiedTerraformAddress, descriptionFile.getAbsolutePath());
            return Optional.empty();
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(descriptionFile))) {
            String asciiDoc = IOUtils.toString(reader);
            return Optional.of(asciiDoc);
        } catch (IOException e) {
            LOGGER.warn("Could not read description file at '{}'", descriptionFile.getAbsolutePath(), e);
        }
        return Optional.empty();
    }
}
