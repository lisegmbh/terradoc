package de.lise.terradoc.core.terraform;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TerraformRuntime {

    public InputStream fetchStateJson(ExecutionConfiguration executionConfiguration) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("terraform", "show", "-json");
        Optional<File> workingDirectoryOptional = executionConfiguration.getWorkingDirectory().map(File::new);
        if(workingDirectoryOptional.isPresent()) {
            File workingDirectory = workingDirectoryOptional.get();
            if(!workingDirectory.exists()) {
                throw new FileNotFoundException(String.format("The working directory '%s' does not exist.", workingDirectory.getAbsolutePath()));
            }
            if(!workingDirectory.isDirectory()) {
                throw new FileNotFoundException(String.format("The given working directory (%s) is not a directory.", workingDirectory.getAbsolutePath()));
            }
            processBuilder = processBuilder.directory(workingDirectory);
        }
        processBuilder = processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = processBuilder.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(InputStream in = process.getInputStream()) {
            IOUtils.copy(in, baos);
        }

        int result = process.waitFor();
        if(result != 0) {
            throw new IOException(String.format("Terraform executable exited with non zero exit code (%d).", result));
        }

        return new ByteArrayInputStream(baos.toByteArray());
    }

}
