package de.lise.terradoc.core.report.elements;

import org.apache.commons.text.StringEscapeUtils;

import java.io.StringWriter;

public class TextElement implements ReportElement {

    private final String text;

    public TextElement(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append(StringEscapeUtils.escapeHtml4(text)).append(" ");
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(text).append(" ");
    }
}
