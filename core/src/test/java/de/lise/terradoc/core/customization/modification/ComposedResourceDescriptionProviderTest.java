package de.lise.terradoc.core.customization.modification;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComposedResourceDescriptionProviderTest {
    @Test
    void getResourceDescriptionShouldReturnEmptyOptionalIfNoProviderReturnsAValue() {
        // Arrange
        ResourceDescriptionProvider p1 = mock(ResourceDescriptionProvider.class);
        ResourceDescriptionProvider p2 = mock(ResourceDescriptionProvider.class);

        when(p1.getAsciidocDescription(any())).thenReturn(Optional.empty());
        when(p2.getAsciidocDescription(any())).thenReturn(Optional.empty());

        List<ResourceDescriptionProvider> providers = Arrays.asList(p1, p2);
        ResourceDescriptionProvider resourceDescriptionProvider = new ComposedResourceDescriptionProvider(providers);

        // Act
        Optional<String> resourceDescription = resourceDescriptionProvider.getAsciidocDescription("some_resource");

        // Assert
        assertThat(resourceDescription).isEmpty();
        verify(p1, times(1)).getAsciidocDescription("some_resource");
        verify(p2, times(1)).getAsciidocDescription("some_resource");
    }

    @Test
    void getResourceDescriptionShouldReturnOnFirstResultWithoutInvokingTheFollowinProviders() {
        // Arrange
        ResourceDescriptionProvider p1 = mock(ResourceDescriptionProvider.class);
        ResourceDescriptionProvider p2 = mock(ResourceDescriptionProvider.class);

        String testDescription = "some description";

        when(p1.getAsciidocDescription(any())).thenReturn(Optional.of(testDescription));
        when(p2.getAsciidocDescription(any())).thenReturn(Optional.empty());

        List<ResourceDescriptionProvider> providers = Arrays.asList(p1, p2);
        ResourceDescriptionProvider resourceDescriptionProvider = new ComposedResourceDescriptionProvider(providers);

        // Act
        Optional<String> resourceDescription = resourceDescriptionProvider.getAsciidocDescription("some_resource");

        // Assert
        assertThat(resourceDescription).isNotEmpty().get().isEqualTo(testDescription);
        verify(p1, times(1)).getAsciidocDescription("some_resource");
        verify(p2, never()).getAsciidocDescription("some_resource");
    }
}