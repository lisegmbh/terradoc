package de.lise.terradoc.core.customization.modification;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.lise.terradoc.core.TerraformAddressBuilder;
import de.lise.terradoc.core.state.TerraformResource;
import de.lise.terradoc.core.state.VirtualTerraformResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

final class CompiledResourceCreatorConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompiledResourceCreatorConfiguration.class);

    private final String virtualType;
    private final Pattern typePattern;
    private final Pattern providerPattern;
    private final JsonPath valuesPath;
    private final JsonPath namePath;

    public CompiledResourceCreatorConfiguration(ResourceCreatorConfiguration resourceCreatorConfiguration) {
        this.typePattern = Pattern.compile(resourceCreatorConfiguration.getType());
        this.providerPattern = Pattern.compile(resourceCreatorConfiguration.getProvider());
        this.valuesPath = JsonPath.compile(resourceCreatorConfiguration.getValuesSelector());
        this.namePath = JsonPath.compile(resourceCreatorConfiguration.getNameSelector());
        this.virtualType = resourceCreatorConfiguration.getVirtualType();
    }

    public boolean matches(TerraformResource resource) {
        return typePattern.matcher(resource.getType()).matches() && providerPattern.matcher(resource.getProviderName()).matches();
    }

    public Collection<VirtualTerraformResource> apply(TerraformResource resource) {
        Object values;
        try {
            values = valuesPath.read(resource.getValues());
        } catch (PathNotFoundException e) {
            return Collections.emptyList();
        }

        if (values instanceof List) {
            return createResource(resource, (List<?>)values);
        }
        List<Object> valueList = new ArrayList<>();
        valueList.add(values);
        return createResource(resource, valueList);
    }

    private Collection<VirtualTerraformResource> createResource(TerraformResource originalResource, List<?> values) {
        List<VirtualTerraformResource> virtualResources = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof Map<?, ?>) {
                Map<String, Object> valuesMap = convertMap((Map<?, ?>)value);
                try {
                    String name = namePath.read(value);
                    VirtualTerraformResource virtualResource = new VirtualTerraformResource(originalResource);
                    virtualResource.setValues(valuesMap);
                    virtualResource.setName(name);
                    virtualResource.setProviderName(originalResource.getProviderName());
                    virtualResource.setType(virtualType);
                    virtualResource.setAddress(TerraformAddressBuilder.concat(originalResource.getAddress(), virtualType, name));
                    virtualResource.setDependsOn(new HashSet<>());
                    virtualResource.setMode("virtual");
                    virtualResource.setSchemaVersion(0);
                    virtualResources.add(virtualResource);
                } catch (RuntimeException e) {
                    LOGGER.info("Could not create virtual resource from values at '{}'. Original resource was: {}", valuesPath.getPath(),
                            originalResource.getAddress(), e);
                }
            }
        }
        return virtualResources;
    }

    private Map<String, Object> convertMap(Map<?, ?> rawMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry entry : rawMap.entrySet()) {
            resultMap.put(String.format("%s", entry.getKey()), entry.getValue());
        }
        return resultMap;
    }
}
