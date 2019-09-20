package de.lise.terradoc.core.customization.modification;

import de.lise.terradoc.core.state.TerraformModule;
import de.lise.terradoc.core.state.TerraformResource;
import de.lise.terradoc.core.state.TerraformState;
import de.lise.terradoc.core.state.VirtualTerraformResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceCreator {
    private final List<CompiledResourceCreatorConfiguration> resourceCreatorConfigurations;

    public ResourceCreator(Collection<ResourceCreatorConfiguration> resourceCreatorConfigurations) {
        this.resourceCreatorConfigurations = new ArrayList<>(resourceCreatorConfigurations.stream().map(CompiledResourceCreatorConfiguration::new).collect(Collectors.toList()));
    }

    public void add(Iterable<ResourceCreatorConfiguration> resourceCreatorConfigurations) {
        for(ResourceCreatorConfiguration resourceCreatorConfiguration : resourceCreatorConfigurations) {
            this.resourceCreatorConfigurations.add(new CompiledResourceCreatorConfiguration(resourceCreatorConfiguration));
        }
    }

    public void add(ResourceCreatorConfiguration resourceCreatorConfiguration){
        this.resourceCreatorConfigurations.add(new CompiledResourceCreatorConfiguration(resourceCreatorConfiguration));
    }

    public void createVirtualResources(TerraformState terraformState) {
        if (terraformState.getValues() == null || terraformState.getValues().getRootModule() == null) {
            return;
        }
        Set<TerraformResource> completedResources = new HashSet<>();
        boolean hasChanges;
        do {
            hasChanges = createVirtualResource(completedResources, terraformState.getValues().getRootModule());
        } while (hasChanges);
    }

    private boolean createVirtualResource(Set<TerraformResource> completedResources, TerraformModule module) {
        boolean hasChanges = false;
        List<TerraformResource> createdResources = new ArrayList<>();
        for (TerraformResource resource : module.getResources()) {
            if (completedResources.contains(resource)) {
                continue;
            }
            completedResources.add(resource);
            for (CompiledResourceCreatorConfiguration resourceCreatorConfiguration : resourceCreatorConfigurations) {
                if (resourceCreatorConfiguration.matches(resource)) {
                    Collection<VirtualTerraformResource> virtualResources = resourceCreatorConfiguration.apply(resource);
                    if (!virtualResources.isEmpty()) {
                        hasChanges = true;
                    }
                    for (VirtualTerraformResource virtualTerraformResource : virtualResources) {
                        resource.getDependsOn().add(virtualTerraformResource.getAddress());
                        createdResources.add(virtualTerraformResource);
                    }
                }
            }
        }
        module.getResources().addAll(createdResources);
        for (TerraformModule childModule : module.getChildModules()) {
            if (createVirtualResource(completedResources, childModule)) {
                hasChanges = true;
            }
        }
        return hasChanges;
    }
}
