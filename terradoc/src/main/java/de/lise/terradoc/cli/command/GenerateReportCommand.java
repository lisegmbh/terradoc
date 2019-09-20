package de.lise.terradoc.cli.command;

import com.google.gson.Gson;
import de.lise.terradoc.core.customization.ReportCustomizations;
import de.lise.terradoc.core.customization.modification.ComposedResourceDescriptionProvider;
import de.lise.terradoc.core.customization.modification.FileResourceDescriptionProvider;
import de.lise.terradoc.core.customization.modification.ReportValueProvider;
import de.lise.terradoc.core.customization.modification.ResourceDescriptionProvider;
import de.lise.terradoc.core.customization.modification.ValueSelectorConfiguration;
import de.lise.terradoc.core.graph.ResourceGraph;
import de.lise.terradoc.core.graph.ResourceGraphBuilder;
import de.lise.terradoc.core.io.ResourceLoader;
import de.lise.terradoc.core.report.ReportBuilder;
import de.lise.terradoc.core.report.ReportDocument;
import de.lise.terradoc.core.state.StateReader;
import de.lise.terradoc.core.state.TerraformState;
import de.lise.terradoc.core.terraform.ExecutionConfiguration;
import de.lise.terradoc.core.terraform.TerraformRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(mixinStandardHelpOptions = true, description = "Generates an infrastructure documentation, based on the current terraform state.",
                     name = "generate")
public class GenerateReportCommand implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateReportCommand.class);
    @CommandLine.Parameters(paramLabel = "PATH", defaultValue = "",
                            description = "The path to your terraform root directory. Note that terraform must be fully "
                                    + "initialized and the desired workspace should be preselected.")
    File terraformPath;
    @CommandLine.Option(names = {"-o", "--output"}, defaultValue = "infradoc.html", description = "The output path for the generated report. If the file ends "
            + "with '.html', a HTML report will be created. If it ends with '.adoc', an AsciiDoc file will be generated. If it refers to a directory, an "
            + "infradoc.html and infradoc.adoc will be generated inside the directory. This option can be specified multiple times.")
    File[] output;
    @CommandLine.Option(names = {"--include-json"},
                        description = "If present, the generated report will contain a JSON representation of all resource's values.")
    boolean includeValues = false;
    @CommandLine.Option(names = {"--value-selectors"},
                        description = "The path to a JSON file, containing an array of custom value selectors. This option can " + "be used multiple times.")
    File[] valueSelectorFiles = new File[0];
    @CommandLine.Option(names = {"--clear-selectors"}, description = "If present, the default value selectors will not be applied.")
    boolean clearDefaultSelectors;
    @CommandLine.Option(names = {"--title", "-t"}, defaultValue = "Infrastructure", description = "Controls the title of the generated report.")
    String reportTitle;
    @CommandLine.Option(names = {"--custom-docs", "-d"}, description = "Specifies a directory that should be searched for additional documentation files to "
            + "be included. Can be specified multiple times.")
    File[] customDocumentationSearchPaths = new File[0];

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            LOGGER.error("An unhandled exception occurred. See the stack trace for more information.", e);
        }
    }

    private void execute() throws IOException, InterruptedException {
        ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
        executionConfiguration.setWorkingDirectory(terraformPath.getAbsolutePath());

        TerraformRuntime runtime = new TerraformRuntime();
        TerraformState state;
        try (InputStream inputStream = runtime.fetchStateJson(executionConfiguration)) {
            StateReader stateReader = new StateReader();
            state = stateReader.read(inputStream);
        }

        ResourceLoader resourceLoader = new ResourceLoader(StandardCharsets.UTF_8, new Gson());

        ReportCustomizations reportCustomizations = ReportCustomizations.createDefault(resourceLoader);

        reportCustomizations.setResourceDescriptionProvider(createResourceDescriptionProvider());
        reportCustomizations.setReportTitle(reportTitle);
        reportCustomizations.setIncludeRawResourceValues(includeValues);
        addCustomValueMappins(resourceLoader, reportCustomizations.getReportValueProvider());

        reportCustomizations.getResourceCreator().createVirtualResources(state);
        ResourceGraphBuilder resourceGraphBuilder = new ResourceGraphBuilder();
        ResourceGraph resourceGraph = resourceGraphBuilder.build(state);

        ReportBuilder reportBuilder = new ReportBuilder(reportCustomizations);
        ReportDocument reportDocument = reportBuilder.buildReport(resourceGraph);
        writeReport(reportDocument);
    }

    public ResourceDescriptionProvider createResourceDescriptionProvider() {
        if (customDocumentationSearchPaths == null || customDocumentationSearchPaths.length == 0) {
            customDocumentationSearchPaths = new File[]{terraformPath.getAbsoluteFile()};
        }
        List<ResourceDescriptionProvider> resourceDescriptionProviders = new ArrayList<>();
        for (File f : customDocumentationSearchPaths) {
            LOGGER.info("Added custom document search path: {}", f.getAbsolutePath());
            resourceDescriptionProviders.add(new FileResourceDescriptionProvider(f));
        }
        return new ComposedResourceDescriptionProvider(resourceDescriptionProviders);
    }

    private void addCustomValueMappins(ResourceLoader resourceLoader, ReportValueProvider reportValueProvider) throws IOException {
        if (clearDefaultSelectors) {
            if (valueSelectorFiles == null || valueSelectorFiles.length == 0) {
                LOGGER.warn("You cleared the default value selectors without specifying at least one custom value selector file. Your report will not "
                        + "contain any further resource information.");
            }
            reportValueProvider.clear();
        }
        if (valueSelectorFiles == null) {
            return;
        }
        for (File valueMappingFile : valueSelectorFiles) {
            List<ValueSelectorConfiguration> valueSelectorConfigurations =
                    resourceLoader.readFileAsJsonList(valueMappingFile, ValueSelectorConfiguration.class);
            reportValueProvider.add(valueSelectorConfigurations);
        }
    }

    private void writeReport(ReportDocument reportDocument) throws IOException {
        if (this.output == null) {
            return;
        }
        for (File outputFile : output) {
            if (outputFile.isDirectory()) {

                export(reportDocument, new File(outputFile.getAbsolutePath(), "infradoc.html"));
                export(reportDocument, new File(outputFile.getAbsolutePath(), "infradoc.adoc"));
            } else {
                export(reportDocument, outputFile);
            }
        }
    }

    public void export(ReportDocument document, File file) throws IOException {
        if (!file.getName().endsWith(".html") && !file.getName().endsWith(".adoc")) {
            throw new IOException(String.format("Invalid file name extension: %s. Only .html and .adoc is supported.", file.getName()));
        }
        if (!file.exists()) {
            File parent = file.getAbsoluteFile().getParentFile();
            if (!parent.isDirectory() || !parent.exists()) {
                throw new FileNotFoundException(String.format("The path '%s' could not be found and will not be created automatically."));
            }
        }
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file))) {
            StringWriter stringWriter = new StringWriter();
            if (file.getName().endsWith(".html")) {
                document.appendHtml(stringWriter);
            } else {
                document.appendAsciidoc(stringWriter);
            }
            fileWriter.append(stringWriter.toString());
            LOGGER.info("Report written to '{}'.", file.getAbsolutePath());
        }
    }
}
