package de.lise.terradoc.core.graph;

public class ResourceDependency {

    private ResourceNode source;
    private StateNode destination;

    public ResourceDependency(ResourceNode source, StateNode destination) {
        this.source = source;
        this.destination = destination;
    }

    public StateNode getSource() {
        return source;
    }

    public StateNode getDestination() {
        return destination;
    }
}
