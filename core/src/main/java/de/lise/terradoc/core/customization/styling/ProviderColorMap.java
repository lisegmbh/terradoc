package de.lise.terradoc.core.customization.styling;

import java.util.HashMap;
import java.util.Map;

public class ProviderColorMap {
    public static ProviderColorMap DEFAULT_COLOR_MAP = new ProviderColorMap();
    static {
        DEFAULT_COLOR_MAP
                .put("kubernetes", new ReportColor(51, 107, 228))
                .put("aws", new ReportColor(255, 153, 0));

    }

    private final Map<String, ReportColor> providerColors = new HashMap<>();
    private ReportColor defaultColor = ReportColor.BLACK;

    public ProviderColorMap put(String provider, ReportColor color) {
        providerColors.put(provider, color);
        return this;
    }

    public ReportColor getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(ReportColor defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void clear() {
        providerColors.clear();
    }

    public void remove(String provider) {
        providerColors.remove(provider);
    }

    public ReportColor getColor(String provider) {
        if(providerColors.containsKey(provider)){
            return providerColors.get(provider);
        }
        return defaultColor;
    }
}
