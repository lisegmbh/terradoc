package de.lise.terradoc.core.customization;

import de.lise.terradoc.core.customization.converter.ModuleNameConverter;
import de.lise.terradoc.core.customization.converter.ResourceTypeConverter;
import de.lise.terradoc.core.customization.modification.NoopResourceDescriptionProvider;
import de.lise.terradoc.core.customization.modification.ReportValueProvider;
import de.lise.terradoc.core.customization.modification.ResourceDescriptionProvider;
import de.lise.terradoc.core.customization.modification.ValueSelectorConfiguration;
import de.lise.terradoc.core.customization.modification.ResourceCreatorConfiguration;
import de.lise.terradoc.core.customization.modification.ResourceCreator;
import de.lise.terradoc.core.customization.styling.ProviderColorMap;
import de.lise.terradoc.core.io.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReportCustomizations {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCustomizations.class);
    private ProviderColorMap providerColorMap = ProviderColorMap.DEFAULT_COLOR_MAP;
    private ResourceTypeConverter resourceTypeConverter = new ResourceTypeConverter();
    private ModuleNameConverter moduleNameConverter = new ModuleNameConverter();
    private String styleSheet = "";
    private boolean includeRawResourceValues = false;
    private ReportValueProvider reportValueProvider = new ReportValueProvider(Collections.emptyList());
    private ResourceDescriptionProvider resourceDescriptionProvider = new NoopResourceDescriptionProvider();
    private ResourceCreator resourceCreator = new ResourceCreator(Collections.emptyList());

    private String reportTitle = "Infrastructure Documentation";

    public ResourceCreator getResourceCreator() {
        return resourceCreator;
    }

    public void setResourceCreator(ResourceCreator resourceCreator) {
        this.resourceCreator = resourceCreator;
    }

    public ProviderColorMap getProviderColorMap() {
        return providerColorMap;
    }

    public void setProviderColorMap(ProviderColorMap providerColorMap) {
        this.providerColorMap = providerColorMap;
    }

    public ResourceTypeConverter getResourceTypeConverter() {
        return resourceTypeConverter;
    }

    public void setResourceTypeConverter(ResourceTypeConverter resourceTypeConverter) {
        this.resourceTypeConverter = resourceTypeConverter;
    }

    public ModuleNameConverter getModuleNameConverter() {
        return moduleNameConverter;
    }

    public void setModuleNameConverter(ModuleNameConverter moduleNameConverter) {
        this.moduleNameConverter = moduleNameConverter;
    }

    public String getStyleSheet() {
        return styleSheet;
    }

    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }

    public ReportValueProvider getReportValueProvider() {
        return reportValueProvider;
    }

    public void setReportValueProvider(ReportValueProvider reportValueProvider) {
        this.reportValueProvider = reportValueProvider;
    }

    public boolean isIncludeRawResourceValues() {
        return includeRawResourceValues;
    }

    public void setIncludeRawResourceValues(boolean includeRawResourceValues) {
        this.includeRawResourceValues = includeRawResourceValues;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public ResourceDescriptionProvider getResourceDescriptionProvider() {
        return resourceDescriptionProvider;
    }

    public void setResourceDescriptionProvider(ResourceDescriptionProvider resourceDescriptionProvider) {
        this.resourceDescriptionProvider = resourceDescriptionProvider;
    }

    public static ReportCustomizations createDefault(ResourceLoader resourceLoader) {
        ReportCustomizations reportCustomizations = new ReportCustomizations();

        try {
            reportCustomizations.styleSheet = resourceLoader.readResourceAsString(ResourceLoader.DEFAULT_STYLESHEET_RESOURCE_NAME);
        } catch (IOException e) {
            LOGGER.warn("Could not load the default stylesheet resource.", e);
        }

        try {
            List<ValueSelectorConfiguration> reportValueSelectorConfigurations =
                    resourceLoader.readResourceAsJsonList(ResourceLoader.DEFAULT_VALUE_SELECTORS_RESOURCE_NAME, ValueSelectorConfiguration.class);
            reportCustomizations.reportValueProvider = new ReportValueProvider(reportValueSelectorConfigurations);
        } catch (IOException e) {
            LOGGER.warn("Could not load the default report value selectors.", e);
        }

        try {
            List<ResourceCreatorConfiguration> resourceCreatorConfigurations =
                    resourceLoader.readResourceAsJsonList(ResourceLoader.DEFAULT_RESOURCE_CREATORS_RESOURCE_NAME, ResourceCreatorConfiguration.class);
            reportCustomizations.resourceCreator = new ResourceCreator(resourceCreatorConfigurations);
        } catch (IOException e) {
            LOGGER.warn("Could not load the default resource creators.", e);
        }

        return reportCustomizations;
    }
}
