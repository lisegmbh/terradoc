package de.lise.terradoc.core.report;

public final class AsciiDocIdentifier {

    private static final String IDENTIFIER_REPLACE_PATTERN = "[^\\w\\-]";

    private AsciiDocIdentifier() {

    }

    public static String toAsciiDocIdentifier(String identifier) {
        return identifier.replaceAll(IDENTIFIER_REPLACE_PATTERN, "_");
    }

}
