package de.lise.terradoc.core.report;

import de.lise.terradoc.core.graph.ResourceGraph;
import de.lise.terradoc.core.customization.ReportCustomizations;
import de.lise.terradoc.core.report.elements.DotDiagramm;
import de.lise.terradoc.core.state.TerraformModule;
import de.lise.terradoc.core.state.TerraformResource;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

public class DotDiagramBuilder {
    private final ReportCustomizations reportCustomizations;

    public DotDiagramBuilder(ReportCustomizations reportCustomizations) {
        this.reportCustomizations = reportCustomizations;
    }

    public DotDiagramm create(ResourceGraph resourceGraph) {
        return create(resourceGraph, resourceGraph.getState().getValues().getRootModule());
    }

    public DotDiagramm create(ResourceGraph resourceGraph, TerraformModule terraformModule) {
        Graph g = graph("main").strict().directed().graphAttr().with(RankDir.LEFT_TO_RIGHT);
        g = buildGraph(resourceGraph, g, terraformModule);

        return new DotDiagramm(g);
    }

    private Graph buildGraph(ResourceGraph resources, Graph graph, TerraformModule module) {
        List<LinkSource> resourceNodes = new ArrayList<>();
        resourceNodes.add(node("fake:" + resources.getFullAddress(module)).with(Style.INVIS).with("label", ""));
        if (module.getResources() != null) {
            for (TerraformResource resource : module.getResources()) {
                Node node = node(resources.getFullAddress(resource)).with("label", resource.getName());
                Collection<TerraformResource> resourceDependencies = resources.getResourceDependencies(resource);
                for (TerraformResource dependency : resourceDependencies) {
                    node = node.link(to(node(resources.getFullAddress(dependency))));
                }
                Collection<TerraformModule> moduleDependencies = resources.getModuleDependencies(resource);
                for (TerraformModule dependency : moduleDependencies) {
                    node = node.link(to(node("fake:" + resources.getFullAddress(dependency))).with("lhead", resources.getFullAddress(dependency)));
                }
                node = styleNode(resource, node, resources.getFullAddress(resource));
                resourceNodes.add(node);
            }
        }
        if (module.getChildModules() != null) {
            for (TerraformModule childModule : module.getChildModules()) {
                Graph subgraph = graph(resources.getFullAddress(childModule)).cluster().directed().graphAttr()
                        .with("label", reportCustomizations.getModuleNameConverter().getReadableModuleName(childModule)).graphAttr().with(Style.DOTTED);
                subgraph = buildGraph(resources, subgraph, childModule);
                subgraph = styleGraph(childModule, subgraph, resources.getFullAddress(childModule));
                resourceNodes.add(subgraph);
            }
        }
        return graph.with(resourceNodes);
    }

    private Graph styleGraph(TerraformModule module, Graph subgraph, String fullQualifiedName) {
        Graph result = subgraph;

        result = result.graphAttr().with("URL", String.format("#%s", fullQualifiedName));

        return result;
    }

    private Node styleNode(TerraformResource resource, Node node, String fullQualifiedName) {
        Node result = node.with("color", reportCustomizations.getProviderColorMap().getColor(resource.getProviderName()).toHexString());

        String resourceTye = reportCustomizations.getResourceTypeConverter().getReadableResourceTyp(resource);

        result = result.with(Label.html(String.format("\"%s\"<BR/><I>%s</I>", resource.getName(), resourceTye)));
        result = result.with(Shape.RECTANGLE);
        result = result.with("URL", String.format("#%s", fullQualifiedName));

        return result;
    }
}
