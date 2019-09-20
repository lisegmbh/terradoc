package de.lise.terradoc.core.state;

public class TerraformState {
    private String formatVersion;
    private String terraformVersion;
    private TerraformStateValues values;

    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getTerraformVersion() {
        return terraformVersion;
    }

    public void setTerraformVersion(String terraformVersion) {
        this.terraformVersion = terraformVersion;
    }

    public TerraformStateValues getValues() {
        return values;
    }

    public void setValues(TerraformStateValues values) {
        this.values = values;
    }
}
