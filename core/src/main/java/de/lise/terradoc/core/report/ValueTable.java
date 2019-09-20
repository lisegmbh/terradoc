package de.lise.terradoc.core.report;

import de.lise.terradoc.core.customization.modification.ReportValue;
import de.lise.terradoc.core.report.elements.ReportElement;
import de.lise.terradoc.core.report.elements.TextElement;

import java.io.StringWriter;
import java.util.List;

public class ValueTable implements ReportElement {

    private final Iterable<ReportValue> reportValues;

    public ValueTable(Iterable<ReportValue> reportValues) {
        this.reportValues = reportValues;
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append("<table class=\"tableblock frame-all grid-all stretch\">");
        writer.append("<colgroup>\n" + "<col style=\"width: 37.5%;\">\n" + "<col style=\"width: 62.5%;\">\n" + "</colgroup>");
        writer.append("<tr><th class=\"tableblock halign-left valign-top\">Attribute</th><th class=\"tableblock halign-left valign-top\">Value</th></tr>");
        for(ReportValue reportValue : reportValues) {
            writer.append("<tr>");
            writer.append("<td class=\"tableblock halign-left valign-top\">");
            new TextElement(reportValue.getName()).appendHtml(writer);
            writer.append("</td>");
            writer.append("<td class=\"tableblock halign-left valign-top\">");
            List<String> values = reportValue.getValues();
            if(values.size() == 1) {
                new TextElement(values.get(0)).appendHtml(writer);
            } else if(values.size() > 1) {
                writer.append("<ul>");
                    for(String value : values) {
                        writer.append("<li>");
                        new TextElement(value).appendHtml(writer);
                        writer.append("</li>");
                    }
                writer.append("</ul>");
            }
            writer.append("</td>");
            writer.append("</tr>");
        }
        writer.append("</table>");
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(NEW_LINE).append(NEW_LINE);
        writer.append("[cols=\"3,5a\"]").append(NEW_LINE);
        writer.append("|===").append(NEW_LINE);
        writer.append("| Attribute | Value").append(NEW_LINE);
        for(ReportValue reportValue : reportValues) {
            writer.append(NEW_LINE);
            writer.append("|").append(reportValue.getName()).append(NEW_LINE);
            List<String> values = reportValue.getValues();
            writer.append("|");
            if(values.size() == 1) {
                writer.append(values.get(0)).append(NEW_LINE);
            } else if(values.size() > 1) {
                for(String value : values) {
                    writer.append("* ").append(value).append(NEW_LINE);
                }
            } else {
                writer.append(NEW_LINE);
            }
        }
        writer.append("|===").append(NEW_LINE);
    }
}
