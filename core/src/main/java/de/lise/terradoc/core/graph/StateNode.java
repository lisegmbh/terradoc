package de.lise.terradoc.core.graph;

public abstract class StateNode {

    private final String fullAddress;
    private final String parentAddress;

    public StateNode(String parentAddress, String fullAddress) {
        this.parentAddress = parentAddress;
        this.fullAddress = fullAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getParentAddress() {
        return parentAddress;
    }

    public abstract String getAddress();
}
