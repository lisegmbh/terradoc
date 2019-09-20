package de.lise.terradoc.core.customization.modification;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NoopResourceDescriptionProviderTest {

    @Test
    void shouldAlwaysReturnAnEmptyOptional() {
        // Arrange
        ResourceDescriptionProvider resourceDescriptionProvider = new NoopResourceDescriptionProvider();

        // Act
        Optional<String> description = resourceDescriptionProvider.getAsciidocDescription("null_resource.test_resource_2");

        // Assert
        assertThat(description).isEmpty();
    }
}