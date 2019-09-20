package de.lise.terradoc.core.report.elements;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CompositionElement implements ReportElement, TraversableElement {


    @Override
    public void appendHtml(StringWriter writer) {
        for (ReportElement element : getElements()) {
            element.appendHtml(writer);
        }
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        for (ReportElement element : getElements()) {
            element.appendAsciidoc(writer);
        }
    }

    @Override
    public Collection<? extends ReportElement> getAllChildren() {
        List<ReportElement> result = new ArrayList<>();
        for (ReportElement element : getElements()) {
            result.add(element);
        }
        return result;
    }

    protected abstract Iterable<ReportElement> getElements();

}
