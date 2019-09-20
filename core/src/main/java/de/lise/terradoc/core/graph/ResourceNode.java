package de.lise.terradoc.core.graph;

import de.lise.terradoc.core.state.TerraformResource;

public class ResourceNode extends StateNode {

    private final TerraformResource terraformResource;

    public ResourceNode(String parentAddress, String fullAddress, TerraformResource terraformResource) {
        super(parentAddress, fullAddress);
        this.terraformResource = terraformResource;
    }

    public TerraformResource getTerraformResource() {
        return terraformResource;
    }

    @Override
    public String getAddress() {
        return terraformResource.getAddress();
    }
}
