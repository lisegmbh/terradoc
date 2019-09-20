package de.lise.terradoc.core.report.elements;

import de.lise.terradoc.core.report.AsciiDocIdentifier;

import java.io.StringWriter;

public class InternalReferenceElement implements ReportElement {

    private final String targetIdentifier;
    private final String referenceText;

    public InternalReferenceElement(String targetIdentifier, String referenceText) {
        this.targetIdentifier = targetIdentifier;
        this.referenceText = referenceText;
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append(String.format("<a href=\"#%s\">", targetIdentifier));
        new TextElement(referenceText).appendHtml(writer);
        writer.append("</a>");
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(" ");
        writer.append(String.format("<<%s,%s>>", AsciiDocIdentifier.toAsciiDocIdentifier(targetIdentifier), referenceText));
    }
}
