package de.lise.terradoc.core.graph;

import de.lise.terradoc.core.TerraformAddressBuilder;
import de.lise.terradoc.core.state.TerraformModule;
import de.lise.terradoc.core.state.TerraformResource;
import de.lise.terradoc.core.state.TerraformState;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class ResourceGraphBuilder {

    public ResourceGraph build(TerraformState state) {
        Set<StateNode> nodes = new HashSet<>();

        if (state.getValues() != null && state.getValues().getRootModule() != null) {
            traverse("", nodes, state.getValues().getRootModule());
        }

        Set<ResourceDependency> dependencies = makeDependencies(nodes);
        return new ResourceGraph(state, nodes, dependencies);
    }

    public ResourceGraph build(TerraformState state, TerraformModule module) {
        Set<StateNode> nodes = new HashSet<>();
        traverse(module.getAddress(), nodes, module);
        Set<ResourceDependency> dependencies = makeDependencies(nodes);

        return new ResourceGraph(state, nodes, dependencies);
    }

    private Set<ResourceDependency> makeDependencies(Set<StateNode> nodes) {
        Set<ResourceDependency> dependencies = new HashSet<>();
        for (StateNode node : nodes) {
            if (!(node instanceof ResourceNode)) {
                continue;
            }
            ResourceNode resourceNode = (ResourceNode)node;
            TerraformResource resource = resourceNode.getTerraformResource();
            if (resource.getDependsOn() == null) {
                continue;
            }
            for (String reference : resource.getDependsOn()) {
                Optional<StateNode> target = findTarget(reference, nodes);
                if (!target.isPresent()) {
                    throw new NoSuchElementException(String.format("Could not find the target '%s'.", reference));
                }
                dependencies.add(new ResourceDependency(resourceNode, target.get()));
            }
        }
        return dependencies;
    }

    private Optional<StateNode> findTarget(String referenceName, Set<StateNode> resources) {
        return resources.stream().filter(r -> referenceName.equals(r.getFullAddress()) || referenceName.equals(r.getAddress())).findFirst();
    }

    private void traverse(String name, Set<StateNode> nodes, TerraformModule module) {
        if (module == null) {
            return;
        }
        nodes.add(new ModuleNode(name, module.getAddress(), module));
        for (TerraformResource resource : module.getResources()) {
            nodes.add(new ResourceNode(name, TerraformAddressBuilder.concat(name, resource.getAddress()), resource));
        }
        if (module.getChildModules() != null) {
            for (TerraformModule childModule : module.getChildModules()) {
                traverse(childModule.getAddress(), nodes, childModule);
            }
        }
    }

}
