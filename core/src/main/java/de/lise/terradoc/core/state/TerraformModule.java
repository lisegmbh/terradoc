package de.lise.terradoc.core.state;

import java.util.ArrayList;
import java.util.List;

public class TerraformModule {
    private List<TerraformModule> childModules = new ArrayList<>();
    private List<TerraformResource> resources = new ArrayList<>();
    private String address;

    public List<TerraformModule> getChildModules() {
        return childModules;
    }

    public void setChildModules(List<TerraformModule> childModules) {
        this.childModules = childModules;
    }

    public List<TerraformResource> getResources() {
        return resources;
    }

    public void setResources(List<TerraformResource> resources) {
        this.resources = resources;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
