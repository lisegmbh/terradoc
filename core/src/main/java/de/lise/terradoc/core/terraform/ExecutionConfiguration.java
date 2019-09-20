package de.lise.terradoc.core.terraform;

import java.util.Optional;

public class ExecutionConfiguration {

    private String workingDirectory;

    public Optional<String> getWorkingDirectory() {
        return Optional.ofNullable(workingDirectory);
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}
