package de.lise.terradoc.core.report.elements;

import de.lise.terradoc.core.report.ReportDocument;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TableOfContents implements ReportElement {

    private final ReportDocument reportDocument;

    public TableOfContents(ReportDocument reportDocument) {
        this.reportDocument = reportDocument;
    }

    private void getHeadings(List<HeadingElement> result, TraversableElement element) {
        for(ReportElement child : element.getAllChildren()) {
            if(child instanceof HeadingElement) {
                result.add((HeadingElement) child);
            } else if(child instanceof TraversableElement) {
                getHeadings(result, (TraversableElement) child);
            }
        }
    }

    @Override
    public void appendHtml(StringWriter writer) {
        List<HeadingElement> headings = new ArrayList<>();
        getHeadings(headings, reportDocument);

        writer.append("<div id=\"toc\" class=\"toc\"><div id=\"toctitle\">Table of Contents</div><ul>");
        for(HeadingElement e : headings) {

            writer.append(String.format("<li style=\"padding-left: %dem\"><a href=\"#%s\">", e.getLevel() * 2, e.getIdentifier())).append(e.getPrefix()).append(" ").append(e.getTextElement().getText()).append("</a></li>");
        }
        writer.append("</div></ul>");
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(":toc:");
        writer.append(NEW_LINE).append(NEW_LINE);
    }
}
