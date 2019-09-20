package de.lise.terradoc.core.graph;

import de.lise.terradoc.core.state.TerraformModule;
import de.lise.terradoc.core.state.TerraformResource;
import de.lise.terradoc.core.state.TerraformState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceGraph {

    private final Set<StateNode> stateNodes;
    private final Set<ResourceDependency> dependencies;
    private final TerraformState state;

    public ResourceGraph(TerraformState state, Set<StateNode> stateNodes, Set<ResourceDependency> dependencies) {
        this.state = state;
        this.stateNodes = stateNodes;
        this.dependencies = dependencies;
    }

    public TerraformState getState() {
        return state;
    }

    public Collection<TerraformModule> getModules() {
        return stateNodes.stream().filter(n -> n instanceof ModuleNode).map(n -> ((ModuleNode)n).getModule()).collect(Collectors.toList());
    }

    public Collection<TerraformResource> getResources() {
        return stateNodes.stream().filter(n -> n instanceof ResourceNode).map(n -> ((ResourceNode) n).getTerraformResource()).collect(Collectors.toList());
    }

    public Optional<ResourceNode> getResourceNode(TerraformResource resource) {
        return stateNodes.stream().filter(n -> n instanceof ResourceNode).map(n -> (ResourceNode) n).filter(n -> n.getTerraformResource() == resource).findFirst();
    }

    public Optional<ModuleNode> getModuleNode(TerraformModule module) {
        return stateNodes.stream().filter(n -> n instanceof ModuleNode).map(n -> (ModuleNode) n).filter(n -> n.getModule() == module).findFirst();
    }

    public Optional<TerraformModule> findModuleFor(TerraformResource terraformResource) {
        return getModules().stream().filter(m -> m.getResources().contains(terraformResource)).findFirst();
    }


    public String getFullAddress(TerraformResource resource) {
        Optional<ResourceNode> node = getResourceNode(resource);
        return node.map(ResourceNode::getFullAddress).orElse(resource.getAddress());
    }

    public String getFullAddress(TerraformModule module) {
        Optional<ModuleNode> node = getModuleNode(module);
        return node.map(ModuleNode::getFullAddress).orElse(module.getAddress());
    }

    public Collection<TerraformResource> getResourceDependencies(TerraformResource source) {
        Optional<ResourceNode> node = getResourceNode(source);
        if(!node.isPresent()) {
            return Collections.emptySet();
        }
        return dependencies.stream()
                .filter(d -> d.getSource() == node.get())
                .filter(d -> d.getDestination() instanceof ResourceNode)
                .map(d -> ((ResourceNode) d.getDestination()).getTerraformResource())
                .collect(Collectors.toSet());
    }

    public Collection<TerraformModule> getModuleDependencies(TerraformResource source) {
        Optional<ResourceNode> node = getResourceNode(source);
        if(!node.isPresent()) {
            return Collections.emptySet();
        }
        return dependencies.stream()
                .filter(d -> d.getSource() == node.get())
                .filter(d -> d.getDestination() instanceof ModuleNode)
                .map(d -> ((ModuleNode) d.getDestination()).getModule())
                .collect(Collectors.toSet());
    }

}
