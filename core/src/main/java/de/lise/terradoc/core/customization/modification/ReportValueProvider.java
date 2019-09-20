package de.lise.terradoc.core.customization.modification;

import de.lise.terradoc.core.state.TerraformResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportValueProvider {
    private final List<CompiledValueSelector> compiledValueSelectors = new ArrayList<>();

    public ReportValueProvider(Collection<ValueSelectorConfiguration> mappings) {
        add(mappings);
    }

    public void add(Iterable<ValueSelectorConfiguration> configurations) {
        for (ValueSelectorConfiguration valueSelectorConfiguration : configurations) {
            add(valueSelectorConfiguration);
        }
    }

    public void add(ValueSelectorConfiguration configuration) {
        this.compiledValueSelectors.add(new CompiledValueSelector(configuration));
    }

    public void clear() {
        compiledValueSelectors.clear();
    }

    public List<ReportValue> getValues(TerraformResource resource) {
        List<ReportValue> reportValues = new ArrayList<>();
        for (CompiledValueSelector compiledValueSelector : compiledValueSelectors) {
            if (compiledValueSelector.matches(resource)) {
                List<String> result = compiledValueSelector.apply(resource.getValues());
                if (result != null && !result.isEmpty()) {
                    reportValues.add(new ReportValue(compiledValueSelector.getMapping().getName(), result));
                }
            }
        }
        return reportValues;
    }
}
