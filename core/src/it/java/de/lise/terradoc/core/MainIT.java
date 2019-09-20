package de.lise.terradoc.core;

import com.google.gson.Gson;
import de.lise.terradoc.core.graph.ResourceGraph;
import de.lise.terradoc.core.graph.ResourceGraphBuilder;
import de.lise.terradoc.core.io.ResourceLoader;
import de.lise.terradoc.core.report.ReportBuilder;
import de.lise.terradoc.core.report.ReportDocument;
import de.lise.terradoc.core.customization.ReportCustomizations;
import de.lise.terradoc.core.customization.modification.FileResourceDescriptionProvider;
import de.lise.terradoc.core.state.StateReader;
import de.lise.terradoc.core.state.TerraformState;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class MainIT {


    @Test
    public void reportShouldBeGeneratedCorrectly() throws IOException, InterruptedException {
        // Arrange
        ResourceLoader resourceLoader = new ResourceLoader(StandardCharsets.UTF_8, new Gson());
        ReportCustomizations reportCustomizations = ReportCustomizations.createDefault(resourceLoader);
        StateReader reader = new StateReader();
        TerraformState terraformState;

        // Act
        try(InputStream in = resourceLoader.getResourceStream("test-state.json")) {
            terraformState = reader.read(in);
        }

        if(reportCustomizations.getResourceCreator() != null) {
            reportCustomizations.getResourceCreator().createVirtualResources(terraformState);
        }

        ResourceGraphBuilder graphBuilder = new ResourceGraphBuilder();
        ResourceGraph resourceGraph = graphBuilder.build(terraformState);
        reportCustomizations.setResourceDescriptionProvider(new FileResourceDescriptionProvider(TestHelper.getInitializedWorkingDirectory()));

        ReportBuilder reportBuilder = new ReportBuilder(reportCustomizations);
        ReportDocument doc = reportBuilder.buildReport(resourceGraph);

        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(TestHelper.getResultsDirectory(), "example.html")))) {
            StringWriter writer = new StringWriter();
            doc.appendHtml(writer);
            outputStreamWriter.write(writer.toString());
        }


        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(TestHelper.getResultsDirectory(), "example.adoc")))) {
            StringWriter writer = new StringWriter();
            doc.appendAsciidoc(writer);
            outputStreamWriter.write(writer.toString());
        }

        // Assert
        assertThat(doc).isNotNull();
    }

}
