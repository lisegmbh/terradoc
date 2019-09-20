package de.lise.terradoc.core.terraform;

import de.lise.terradoc.core.TestHelper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class TerraformRuntimeIT {
    @Test
    void terraformRuntimeShouldReturnStateIfWorkingDirectoryIsInValidState() throws IOException, InterruptedException {
        // Arrange
        ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
        String workingDirectory = TestHelper.getInitializedWorkingDirectory().getAbsolutePath();
        executionConfiguration.setWorkingDirectory(workingDirectory);
        TerraformRuntime terraformRuntime = new TerraformRuntime();
        String state;

        // Act
        try (InputStream in = terraformRuntime.fetchStateJson(executionConfiguration)) {
            state = IOUtils.toString(in, StandardCharsets.UTF_8);
        }

        assertThat(state).isNotEmpty();
    }
}
