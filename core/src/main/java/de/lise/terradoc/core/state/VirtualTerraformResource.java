package de.lise.terradoc.core.state;

public class VirtualTerraformResource extends TerraformResource {

    private final TerraformResource originalResource;

    public VirtualTerraformResource(TerraformResource createdFrom) {
        this.originalResource = createdFrom;
    }

    public TerraformResource getOriginalResource() {
        return originalResource;
    }
}
