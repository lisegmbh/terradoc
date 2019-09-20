package de.lise.terradoc.core.report.elements;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;

import java.io.StringWriter;

public class DotDiagramm implements ReportElement {

    private final Graph graph;

    public DotDiagramm(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void appendHtml(StringWriter writer) {
        writer.append(Graphviz.fromGraph(graph).render(Format.SVG).toString());
    }

    @Override
    public void appendAsciidoc(StringWriter writer) {
        writer.append(NEW_LINE);
        writer.append("[graphviz]").append(NEW_LINE);
        writer.append("....").append(NEW_LINE);
        writer.append(Graphviz.fromGraph(graph).render(Format.DOT).toString());
        writer.append(NEW_LINE).append("....").append(NEW_LINE);
    }
}
