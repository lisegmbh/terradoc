package de.lise.terradoc.cli;

import de.lise.terradoc.cli.command.ExportValueSelectorsCommand;
import de.lise.terradoc.cli.command.GenerateReportCommand;
import de.lise.terradoc.cli.command.MainCommand;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new MainCommand());
        commandLine.addSubcommand(new GenerateReportCommand());
        commandLine.addSubcommand(new ExportValueSelectorsCommand());
        commandLine.execute(args);
    }
}
