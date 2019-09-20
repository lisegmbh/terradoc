package de.lise.terradoc.core.customization.styling;

public class ReportColor {

    private final int red;
    private final int green;
    private final int blue;

    public static final ReportColor RED = new ReportColor(255,0,0);
    public static final ReportColor BLUE = new ReportColor(0, 0, 255);
    public static final ReportColor GREEN = new ReportColor(0, 255, 0);
    public static final ReportColor BLACK = new ReportColor(0,0,0);
    public static final ReportColor WHITE = new ReportColor(255, 255, 255);


    public ReportColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public String toHexString() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }

}
