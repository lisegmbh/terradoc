package de.lise.terradoc.core.report.elements;

import java.util.Collection;

public interface TraversableElement {

    Collection<? extends ReportElement> getAllChildren();

}
