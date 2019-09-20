package de.lise.terradoc.core.report.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReportSpan extends CompositionElement implements ReportContainerElement {

    private final List<ReportElement> children = new ArrayList<>();

    @Override
    public ReportContainerElement add(ReportElement... reportElements) {
        children.addAll(Arrays.asList(reportElements));
        return this;
    }

    @Override
    protected Iterable<ReportElement> getElements() {
        return children;
    }

    @Override
    public Collection<? extends ReportElement> getAllChildren() {
        return children;
    }
}
