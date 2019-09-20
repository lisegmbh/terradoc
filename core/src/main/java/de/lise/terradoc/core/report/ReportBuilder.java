package de.lise.terradoc.core.report;

import de.lise.terradoc.core.graph.ResourceGraph;
import de.lise.terradoc.core.graph.ResourceGraphBuilder;
import de.lise.terradoc.core.customization.ReportCustomizations;
import de.lise.terradoc.core.customization.modification.ReportValue;
import de.lise.terradoc.core.report.elements.AsciiDocLiteral;
import de.lise.terradoc.core.report.elements.CodeElement;
import de.lise.terradoc.core.report.elements.DotDiagramm;
import de.lise.terradoc.core.report.elements.HeadingElement;
import de.lise.terradoc.core.report.elements.InternalReferenceElement;
import de.lise.terradoc.core.report.elements.ReportSpan;
import de.lise.terradoc.core.report.elements.SectionElement;
import de.lise.terradoc.core.report.elements.TableOfContents;
import de.lise.terradoc.core.report.elements.TextElement;
import de.lise.terradoc.core.state.TerraformModule;
import de.lise.terradoc.core.state.TerraformResource;
import de.lise.terradoc.core.state.TerraformState;
import de.lise.terradoc.core.state.VirtualTerraformResource;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ReportBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportBuilder.class);

    private final ReportCustomizations reportCustomizations;

    public ReportBuilder(ReportCustomizations reportCustomizations) {
        this.reportCustomizations = reportCustomizations;
    }

    public ReportDocument buildReport(ResourceGraph graph) {

        ResourceGraphBuilder resourceGraphBuilder = new ResourceGraphBuilder();

        DotDiagramBuilder dotDiagramBuilder = new DotDiagramBuilder(reportCustomizations);

        ReportDocument doc = new ReportDocument(reportCustomizations.getReportTitle());
        doc.setCustomStylesheet(reportCustomizations.getStyleSheet());

        SectionElement infrastructureSection = new SectionElement(doc, new HeadingElement(doc,0, "infrastructure", reportCustomizations.getReportTitle()));
        infrastructureSection.getContent().add(new TableOfContents(doc));

        buildModule(dotDiagramBuilder, resourceGraphBuilder, infrastructureSection, graph.getState(), graph.getState().getValues().getRootModule());

        doc.getContent().add(infrastructureSection);
        return doc;
    }

    private void buildModule(DotDiagramBuilder diagramBuilder, ResourceGraphBuilder graphBuilder, SectionElement span, TerraformState state,
            TerraformModule module) {
        ResourceGraph resourceGraph = graphBuilder.build(state, module);
        DotDiagramm resourceDiagram = diagramBuilder.create(resourceGraph, module);

        SectionElement subsection;
        if (module.getAddress() == null || module.getAddress().isEmpty()) {
            subsection = span.subsection("main-module", "Main module");
            subsection.getContent().add(resourceDiagram);
        } else {
            String moduleName = reportCustomizations.getModuleNameConverter().getReadableModuleName(module);
            String fullModuleName = resourceGraph.getFullAddress(module);
            subsection = span.subsection(fullModuleName, String.format("\"%s\" module", moduleName));
            subsection.getContent().add(resourceDiagram);
        }

        for (TerraformResource resource : module.getResources()) {
            buildResource(resourceGraph, subsection, resource);
        }

        for (TerraformModule childModule : module.getChildModules()) {
            buildModule(diagramBuilder, graphBuilder, span, state, childModule);
        }
    }

    private void buildResource(ResourceGraph resourceGraph, SectionElement parentSection, TerraformResource resource) {
        String heading = String.format("%s \"%s\"", reportCustomizations.getResourceTypeConverter().getReadableResourceTyp(resource), resource.getName());
        String fullQulifiedAddress = resourceGraph.getFullAddress(resource);
        LOGGER.info("Processing resource: {}", fullQulifiedAddress);

        SectionElement resourceSection = parentSection.subsection(fullQulifiedAddress, heading);

        if(resource instanceof VirtualTerraformResource) {
            VirtualTerraformResource virtualTerraformResource = (VirtualTerraformResource) resource;
            TerraformResource originalResource = virtualTerraformResource.getOriginalResource();
            ReportSpan reportSpan = new ReportSpan();
            reportSpan.add(new TextElement("created from"));
            reportSpan.add(new InternalReferenceElement(resourceGraph.getFullAddress(originalResource), originalResource.getName()));
            resourceSection.getContent().add(reportSpan);
        }

        if(reportCustomizations.isIncludeRawResourceValues()) {
            resourceSection.getContent().add(new CodeElement(new GsonBuilder().setPrettyPrinting().create().toJson(resource.getValues())));
        }

        if(resource.getValues() != null && reportCustomizations.getReportValueProvider() != null) {
            List<ReportValue> reportValues = reportCustomizations.getReportValueProvider().getValues(resource);
            if(!reportValues.isEmpty()) {
                resourceSection.getContent().add(new ValueTable(reportValues));
            }
        }

        Optional<String> customDescription = reportCustomizations.getResourceDescriptionProvider().getAsciidocDescription(fullQulifiedAddress);
        if(customDescription.isPresent()) {
            resourceSection.getContent().add(new AsciiDocLiteral(customDescription.get()));
        }

    }
}
