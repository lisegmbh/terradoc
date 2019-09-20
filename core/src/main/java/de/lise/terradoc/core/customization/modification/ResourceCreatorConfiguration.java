package de.lise.terradoc.core.customization.modification;

public class ResourceCreatorConfiguration {
    private String valuesSelector;
    private String nameSelector;
    private String virtualType;
    private String type;
    private String provider;

    public String getValuesSelector() {
        return valuesSelector;
    }

    public void setValuesSelector(String valuesSelector) {
        this.valuesSelector = valuesSelector;
    }

    public String getNameSelector() {
        return nameSelector;
    }

    public void setNameSelector(String nameSelector) {
        this.nameSelector = nameSelector;
    }

    public String getVirtualType() {
        return virtualType;
    }

    public void setVirtualType(String virtualType) {
        this.virtualType = virtualType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
