package de.lise.terradoc.core.report.elements;

import java.io.StringWriter;

public class ParagraphElement extends TextElement {
    public ParagraphElement(String text) {
        super(text);
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append("<p>");
        super.appendHtml(writer);
        writer.append("</p>");
    }
}
