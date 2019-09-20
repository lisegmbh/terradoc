package de.lise.terradoc.core.customization.modification;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.lise.terradoc.core.state.TerraformResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

final class CompiledValueSelector {
    private final ValueSelectorConfiguration mapping;
    private final Pattern typePattern;
    private final Pattern providerPattern;
    private final JsonPath template;
    private final boolean includeEmpty;

    public CompiledValueSelector(ValueSelectorConfiguration mapping) {
        this.mapping = mapping;
        this.typePattern = Pattern.compile(mapping.getType());
        this.providerPattern = Pattern.compile(mapping.getProvider());
        this.template = JsonPath.compile(mapping.getTemplate());
        this.includeEmpty = mapping.isIncludeEmpty();
    }

    public boolean matches(TerraformResource resource) {
        return typePattern.matcher(resource.getType()).matches() && providerPattern.matcher(resource.getProviderName()).matches();
    }

    public List<String> apply(Map<String, Object> valuesMap) {
        try {
            Object selectedValue = template.read(valuesMap);
            List<String> result = convertObject(selectedValue);
            if(!includeEmpty && result.stream().allMatch(e -> e == null || e.isEmpty())) {
                return Collections.emptyList();
            }
            return result;
        } catch (PathNotFoundException e) {
            return Collections.emptyList();
        }
    }

    private List<String> convertObject(Object result) {
        if (result instanceof String || result instanceof Integer || result instanceof Double) {
            return Collections.singletonList(convertToString(result));
        }
        if (result instanceof List) {
            return convertList((List)result);
        }

        return Collections.emptyList();
    }

    private List<String> convertList(List<?> list) {
        List<String> stringList = new ArrayList<>();
        for (Object entry : list) {
            stringList.add(convertToString(entry));
        }
        return stringList;
    }

    private static String convertToString(Object value) {
        if (value instanceof String) {
            return (String)value;
        }
        if (value == null) {
            return "";
        }
        if(value instanceof Map<?,?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            List<String> keyValueList = new ArrayList<>();
            for(Map.Entry<?, ?> entry : map.entrySet()) {
                keyValueList.add(String.format("%s: %s", convertToString(entry.getKey()), convertToString(entry.getValue())));
            }
            return String.join(", ", keyValueList);
        }
        if (value instanceof Integer) {
            return String.format("%d", value);
        }
        if (value instanceof Double) {
            double doubleValue = (Double)value;
            if (doubleValue % 1 == 0) {
                return String.format("%d", (long)doubleValue);
            }
            return String.format("%f", value);
        }
        return "" + value;
    }

    public ValueSelectorConfiguration getMapping() {
        return mapping;
    }
}
