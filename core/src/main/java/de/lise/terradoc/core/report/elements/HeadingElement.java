package de.lise.terradoc.core.report.elements;

import de.lise.terradoc.core.report.AsciiDocIdentifier;
import de.lise.terradoc.core.report.ReportDocument;

import java.io.StringWriter;

public class HeadingElement implements ReportElement {

    private final int level;
    private final TextElement textElement;
    private final String identifier;
    private final String prefix;

    public HeadingElement(ReportDocument document, int level, String identifier, String textElement) {
        this.level = level;
        this.identifier = identifier;
        this.textElement = new TextElement(textElement);
        prefix = document.getSectionPrefix(level);
    }

    public int getLevel() {
        return level;
    }

    public TextElement getTextElement() {
        return textElement;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append(String.format("<h%d id=\"%s\">", level+1, identifier)).append(prefix).append(" ");
        textElement.appendHtml(writer);
        writer.append(String.format("</h%d>", level+1));
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(NEW_LINE).append(NEW_LINE);
        writer.append("[#").append(identifier).append("]").append(NEW_LINE);
        for(int i=0; i<=level; i++) {
            writer.append("=");
        }
        writer.append(" ").append(prefix).append(" ");
        textElement.appendAsciidoc(writer);
        writer.append(NEW_LINE);
        writer.append("[[").append(AsciiDocIdentifier.toAsciiDocIdentifier(identifier)).append("]]").append(NEW_LINE);
    }

}
