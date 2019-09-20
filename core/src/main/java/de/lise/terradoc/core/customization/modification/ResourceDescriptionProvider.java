package de.lise.terradoc.core.customization.modification;

import java.util.Optional;

public interface ResourceDescriptionProvider {

    Optional<String> getAsciidocDescription(String fullQualifiedTerraformAddress);

}
