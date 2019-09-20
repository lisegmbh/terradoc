package de.lise.terradoc.core.state;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class StateReaderTest {

    @Test
    public void readShouldParseStateJsonCorrectly() throws IOException {
        // Arrange
        StateReader reader = new StateReader();
        TerraformState terraformState;

        // Act
        try(InputStream in = StateReader.class.getClassLoader().getResourceAsStream("test-state.json")) {
            terraformState = reader.read(in);
        }

        // Assert
        assertThat(terraformState).isNotNull();
        assertThat(terraformState.getValues()).isNotNull();
        assertThat(terraformState.getValues().getRootModule()).isNotNull();
        assertThat(terraformState.getValues().getRootModule().getAddress()).isNull();
    }

}