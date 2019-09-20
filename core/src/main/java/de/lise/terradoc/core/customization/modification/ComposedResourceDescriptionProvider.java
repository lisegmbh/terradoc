package de.lise.terradoc.core.customization.modification;

import java.util.Optional;

public class ComposedResourceDescriptionProvider implements ResourceDescriptionProvider {
    private final Iterable<ResourceDescriptionProvider> providers;

    public ComposedResourceDescriptionProvider(Iterable<ResourceDescriptionProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Optional<String> getAsciidocDescription(String fullQualifiedTerraformAddress) {
        Optional<String> result;
        for (ResourceDescriptionProvider provider : providers) {
            result = provider.getAsciidocDescription(fullQualifiedTerraformAddress);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}