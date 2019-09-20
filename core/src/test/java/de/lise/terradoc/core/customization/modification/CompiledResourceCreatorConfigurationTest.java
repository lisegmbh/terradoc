package de.lise.terradoc.core.customization.modification;

import com.google.gson.Gson;
import de.lise.terradoc.core.state.TerraformResource;
import de.lise.terradoc.core.state.VirtualTerraformResource;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompiledResourceCreatorConfigurationTest {
    private static final Gson GSON = new Gson();

    @Test
    void matchesShouldReturnTrueIfTypeAndProviderPatternsAreMatching() {
        // Arrange
        ResourceCreatorConfiguration resourceCreatorConfiguration = createMatcherTestConfiguration("provider_.*", "type_.*");
        CompiledResourceCreatorConfiguration compiledResourceCreatorConfiguration = new CompiledResourceCreatorConfiguration(resourceCreatorConfiguration);
        TerraformResource resource = mock(TerraformResource.class);
        when(resource.getProviderName()).thenReturn("provider_matching");
        when(resource.getType()).thenReturn("type_matching");

        // Act
        boolean matches = compiledResourceCreatorConfiguration.matches(resource);

        // Assert
        assertThat(matches).isTrue();
        verify(resource, times(1)).getType();
        verify(resource, times(1)).getProviderName();
    }

    @Test
    void matchesShouldReturnFalseIfTypePatternDoesNotMatch() {
        // Arrange
        ResourceCreatorConfiguration configuration = createMatcherTestConfiguration("provider_.*", "type_.*");
        CompiledResourceCreatorConfiguration compiledResourceCreatorConfiguration = new CompiledResourceCreatorConfiguration(configuration);
        TerraformResource resource = mock(TerraformResource.class);
        when(resource.getProviderName()).thenReturn("provider_matching");
        when(resource.getType()).thenReturn("type1_not_matching");

        // Act
        boolean matches = compiledResourceCreatorConfiguration.matches(resource);

        // Assert
        assertThat(matches).isFalse();
        verify(resource, times(1)).getType();
    }

    @Test
    void matchesShouldReturnFalseIfProviderDoesNotMatch() {
        // Arrange
        ResourceCreatorConfiguration configuration = createMatcherTestConfiguration("provider_.*", "type_.*");
        CompiledResourceCreatorConfiguration compiledResourceCreatorConfiguration = new CompiledResourceCreatorConfiguration(configuration);
        TerraformResource resource = mock(TerraformResource.class);
        when(resource.getProviderName()).thenReturn("provider1_not_matching");
        when(resource.getType()).thenReturn("type_matching");

        // Act
        boolean matches = compiledResourceCreatorConfiguration.matches(resource);

        // Assert
        assertThat(matches).isFalse();
        verify(resource, times(1)).getProviderName();
    }

    @Test
    void applyShouldReturnOneResourceIfValueSelectorMatchesOneElmentThatContainsThePropertySelectedByTheNameSelector() {
        // Arrange
        ResourceCreatorConfiguration configuration = createSelectorTestConfiguration("$.name", "$.tmp");
        CompiledResourceCreatorConfiguration compiledResourceCreatorConfiguration = new CompiledResourceCreatorConfiguration(configuration);
        TerraformResource terraformResource = mockSelectorTestResource("{ \"foo\": \"bar\", \"tmp\": { \"a\": 1, \"b\": 2, \"name\": \"i am a name\" } }");

        // Act
        Collection<VirtualTerraformResource> results = compiledResourceCreatorConfiguration.apply(terraformResource);

        // Verrify
        assertThat(results).hasSize(1);
        Optional<VirtualTerraformResource> resourceOptional = results.stream().findFirst();
        assertThat(resourceOptional).isPresent();
        VirtualTerraformResource resource = resourceOptional.get();
        assertThat(resource.getOriginalResource()).isSameAs(terraformResource);
        assertThat(resource.getName()).isEqualTo("i am a name");
        Map<String, Object> values = resource.getValues();
        assertThat(values.get("a")).isEqualTo(1d);
        assertThat(values.get("b")).isEqualTo(2d);
        assertThat(values.get("name")).isEqualTo(resource.getName());
    }

    @Test
    void applyShouldReturnNoResourceIfValueSelectorMatchesAnElementButThePropertySelectedByTheNameSlectorIsMissing() {
        // Arrange
        ResourceCreatorConfiguration configuration = createSelectorTestConfiguration("$.missingName", "$.tmp");
        CompiledResourceCreatorConfiguration compiledResourceCreatorConfiguration = new CompiledResourceCreatorConfiguration(configuration);
        TerraformResource terraformResource = mockSelectorTestResource("{ \"foo\": \"bar\", \"tmp\": { \"a\": 1, \"b\": 2, \"name\": \"i am a name\" } }");

        // Act
        Collection<VirtualTerraformResource> results = compiledResourceCreatorConfiguration.apply(terraformResource);

        // Verrify
        assertThat(results).isEmpty();
    }

    private static TerraformResource mockSelectorTestResource(String valuesJson) {
        TerraformResource terraformResource = mock(TerraformResource.class);
        Map<String, Object> valuesMap = (Map<String, Object>)GSON.fromJson(valuesJson, Map.class);
        when(terraformResource.getValues()).thenReturn(valuesMap);
        when(terraformResource.getType()).thenReturn("type");
        when(terraformResource.getProviderName()).thenReturn("provider");
        return terraformResource;
    }

    private static ResourceCreatorConfiguration createMatcherTestConfiguration(String providerPattern, String typePattern) {
        return createTestConfiguration(providerPattern, typePattern, "test", "$", "$");
    }

    private static ResourceCreatorConfiguration createSelectorTestConfiguration(String nameSelector, String valueSelector) {
        return createTestConfiguration(".*", ".*", "test_type", nameSelector, valueSelector);
    }

    private static ResourceCreatorConfiguration createTestConfiguration(String providerPattern, String typePattern, String virtualType, String nameSelector,
            String valueSelector) {
        ResourceCreatorConfiguration resourceCreatorConfiguration = new ResourceCreatorConfiguration();
        resourceCreatorConfiguration.setType(typePattern);
        resourceCreatorConfiguration.setProvider(providerPattern);
        resourceCreatorConfiguration.setNameSelector(nameSelector);
        resourceCreatorConfiguration.setValuesSelector(valueSelector);
        resourceCreatorConfiguration.setVirtualType(virtualType);
        return resourceCreatorConfiguration;
    }
}