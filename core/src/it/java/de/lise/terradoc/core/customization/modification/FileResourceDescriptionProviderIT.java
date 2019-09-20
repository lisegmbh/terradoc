package de.lise.terradoc.core.customization.modification;

import de.lise.terradoc.core.TestHelper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class FileResourceDescriptionProviderIT {

    @Test
    void shouldDiscoverAdocFilesWhenPresent() throws IOException, InterruptedException {
        // Arrange
        ResourceDescriptionProvider resourceDescriptionProvider = new FileResourceDescriptionProvider(TestHelper.getInitializedWorkingDirectory());

        // Act
        Optional<String> description = resourceDescriptionProvider.getAsciidocDescription("null_resource.test_resource_2");

        // Assert
        assertThat(description).isPresent().isNotEmpty();
    }

    @Test
    void shouldReturnAnEmptyOptionalIfNoAdocFileCouldBeFound() throws IOException, InterruptedException {
        // Arrange
        ResourceDescriptionProvider resourceDescriptionProvider = new FileResourceDescriptionProvider(TestHelper.getInitializedWorkingDirectory());

        // Act
        Optional<String> description = resourceDescriptionProvider.getAsciidocDescription("null_resource.test_resource_1");

        // Assert
        assertThat(description).isEmpty();
    }

}
