package de.lise.terradoc.core.graph;

import de.lise.terradoc.core.state.TerraformModule;

public class ModuleNode extends StateNode {

    private final TerraformModule module;

    public ModuleNode(String parentAddress, String fullAddress, TerraformModule module) {
        super(parentAddress,  fullAddress);
        this.module = module;
    }

    public TerraformModule getModule() {
        return module;
    }

    @Override
    public String getAddress() {
        return module.getAddress();
    }
}
