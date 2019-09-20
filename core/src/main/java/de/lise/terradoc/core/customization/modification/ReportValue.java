package de.lise.terradoc.core.customization.modification;

import java.util.List;

public class ReportValue {

    private String name;

    private List<String> values;

    public ReportValue(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }
}
