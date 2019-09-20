package de.lise.terradoc.core.report.elements;

import java.io.StringWriter;

public interface ReportElement {

    String NEW_LINE = "\r\n";

    void appendHtml(StringWriter writer);
    void appendAsciidoc(StringWriter writer);

}
