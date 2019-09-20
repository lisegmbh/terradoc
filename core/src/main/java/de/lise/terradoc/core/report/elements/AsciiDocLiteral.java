package de.lise.terradoc.core.report.elements;

import java.io.StringWriter;
import java.util.HashMap;

import static org.asciidoctor.Asciidoctor.Factory.create;
import org.asciidoctor.Asciidoctor;

public class AsciiDocLiteral implements ReportElement {

    private final String asciiDocLiteral;
    private final String htmlLiteral;

    public AsciiDocLiteral(String asciiDocLiteral) {
        this.asciiDocLiteral = asciiDocLiteral;
        Asciidoctor asciidoctor = create();
        htmlLiteral = asciidoctor.convert(asciiDocLiteral, new HashMap<>());
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append(htmlLiteral);
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(NEW_LINE).append(NEW_LINE);
        writer.append(asciiDocLiteral);
        writer.append(NEW_LINE);
    }
}
