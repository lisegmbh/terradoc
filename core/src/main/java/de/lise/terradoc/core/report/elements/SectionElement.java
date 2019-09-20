package de.lise.terradoc.core.report.elements;

import de.lise.terradoc.core.report.ReportDocument;

import java.util.Arrays;
import java.util.List;

public class SectionElement extends CompositionElement {

    private final HeadingElement heading;
    private final ReportSpan content = new ReportSpan();
    private final ReportDocument reportDocument;

    public SectionElement(ReportDocument reportDocument, HeadingElement heading) {
        this.heading = heading;
        this.reportDocument = reportDocument;
    }

    public ReportSpan getContent() {
        return content;
    }

    public SectionElement subsection(String identifier, String heading) {
        SectionElement subsection = new SectionElement(reportDocument, new HeadingElement(reportDocument,this.heading.getLevel() + 1, identifier, heading));
        content.add(subsection);
        return subsection;
    }

    @Override
    protected Iterable<ReportElement> getElements() {
        return Arrays.asList(heading, content);
    }
}
