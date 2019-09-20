package de.lise.terradoc.core.report.elements;

import java.io.StringWriter;

public class CodeElement extends TextElement {
    public CodeElement(String text) {
        super(text);
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append("<pre>");

        super.appendHtml(writer);

        writer.append("</pre>");
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(NEW_LINE).append(NEW_LINE);
        writer.append("----").append(NEW_LINE);
        super.appendAsciidoc(writer);
        writer.append(NEW_LINE).append("----").append(NEW_LINE);
    }
}
