package de.lise.terradoc.core.customization.converter;

import de.lise.terradoc.core.state.TerraformModule;

public class ModuleNameConverter {

    public String getReadableModuleName(TerraformModule module) {
        String name = module.getAddress();
        if(name == null || name.isEmpty()) {
            return "Main";
        }
        String[] parts = name.split("\\.");
        return parts[parts.length-1];
    }

}
