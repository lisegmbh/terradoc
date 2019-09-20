package de.lise.terradoc.core.report.elements;

public interface ReportContainerElement extends ReportElement, TraversableElement {

    ReportContainerElement add(ReportElement... reportElements);

}
