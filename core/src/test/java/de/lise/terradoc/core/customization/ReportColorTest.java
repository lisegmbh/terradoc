package de.lise.terradoc.core.customization;

import de.lise.terradoc.core.customization.styling.ReportColor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportColorTest {

    @Test
    void toHexCodeShouldReturn0xFF0000ForRed() {
        // Arrange
        ReportColor reportColor = new ReportColor(255,0,0);

        // Act
        String hexCode = reportColor.toHexString();

        // Asssert
        assertThat(hexCode).isEqualToIgnoringCase("#ff0000");
    }

}