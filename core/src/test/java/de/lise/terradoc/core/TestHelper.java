package de.lise.terradoc.core;

import java.io.File;
import java.io.IOException;

public class TestHelper {

    public static File getRoot() {
        return new File("").getAbsoluteFile().getParentFile(); // Should be /core
    }

    public static File getTestDataDirectory() {
        return new File(getRoot(), "test-data");
    }

    public static File getInitializedWorkingDirectory() throws InterruptedException, IOException {
        File result = new File(getTestDataDirectory(), "initialized");
        File terraformDirectory = new File(result, ".terraform");
        if(!terraformDirectory.exists()) {
            Process process = new ProcessBuilder("terraform", "init").directory(result).inheritIO().start();
            process.waitFor();
        }
        return result;
    }

    public static File getUninitializedWorkingDirectory() {
        return new File(getTestDataDirectory(), "uninitialized");
    }

    public static File getResultsDirectory() {
        return new File(getTestDataDirectory(), "results");
    }

}
