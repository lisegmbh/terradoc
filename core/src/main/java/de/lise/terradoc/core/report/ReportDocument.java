package de.lise.terradoc.core.report;

import de.lise.terradoc.core.report.elements.ReportContainerElement;
import de.lise.terradoc.core.report.elements.ReportElement;
import de.lise.terradoc.core.report.elements.ReportSpan;
import de.lise.terradoc.core.report.elements.TextElement;
import de.lise.terradoc.core.report.elements.TraversableElement;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDocument implements ReportElement, TraversableElement {

    private final String title;
    private final ReportContainerElement content = new ReportSpan();
    private String customStylesheet = "";

    private final Map<Integer, Integer> sectionCount = new HashMap<>();

    public ReportDocument(String title) {
        this.title = title;
    }

    public ReportContainerElement getContent() {
        return content;
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.write("<html><head><title>");
        new TextElement(title).appendHtml(writer);
        writer.write("</title>");
        writer.write("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css\">");
        writer.write("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Open+Sans:300,300italic,400,400italic,600,600italic%7CNoto+Serif:400,400italic,700,700italic%7CDroid+Sans+Mono:400,700\">");
        if(customStylesheet != null && !customStylesheet.isEmpty()) {
            writer.append("<style>").append(customStylesheet).append("</style>");
        }
        writer.write("</head><body><div id=\"content\">");
        content.appendHtml(writer);
        writer.write("</div></body></html>");
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        content.appendAsciidoc(writer);
    }

    public String getCustomStylesheet() {
        return customStylesheet;
    }

    public void setCustomStylesheet(String customStylesheet) {
        this.customStylesheet = customStylesheet;
    }

    @Override
    public Collection<? extends ReportElement> getAllChildren() {
        return Collections.singletonList(content);
    }


    private String getCurrentSectionPrefix(int level) {
        if(!sectionCount.containsKey(level)) {
            sectionCount.put(level, 1);
        }
        if(level <= 0) {
            return String.format("%d", sectionCount.get(level));
        }
        return String.format("%s.%d", getCurrentSectionPrefix(level-1), sectionCount.get(level));
    }

    public String getSectionPrefix(int level) {
        for(Integer key : sectionCount.keySet()) {
            if(key > level) {
                sectionCount.put(key, 0);
            }
        }
        if(!sectionCount.containsKey(level)) {
            sectionCount.put(level, 0);
        }
        sectionCount.put(level, sectionCount.get(level) + 1);
        return getCurrentSectionPrefix(level);
    }
}
