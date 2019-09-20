package de.lise.terradoc.core.customization.modification;

import java.util.Optional;

public class NoopResourceDescriptionProvider implements ResourceDescriptionProvider {
    @Override
    public Optional<String> getAsciidocDescription(String fullQualifiedTerraformAddress) {
        return Optional.empty();
    }
}
