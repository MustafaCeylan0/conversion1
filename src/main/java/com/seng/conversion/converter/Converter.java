package com.seng.conversion.converter;

import com.seng.conversion.customErrorHandler.PythonScriptExecutionException;
import com.seng.conversion.customErrorHandler.UnsupportedFormatException;
import com.seng.conversion.helper.ConversionHelper;
import com.seng.conversion.helper.ConversionData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class Converter {

    public boolean isConversionPossible(String inputFormat, String outputFormat) {
        return ConversionData.conversionHelpers.stream()
                .anyMatch(helper -> helper.getInputFormat().equals(inputFormat) &&
                        helper.getOutputFormat().equals(outputFormat));
    }


    @Async("taskExecutor")
    public CompletableFuture<String> convertFile(String originalInputFile, String originalOutputFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String inputFormat = getFileExtension(originalInputFile);
                String outputFormat = getFileExtension(originalOutputFile);

                if (!isConversionPossible(inputFormat, outputFormat)) {
                    throw new UnsupportedFormatException("Conversion from " + inputFormat + " to " + outputFormat + " is not supported.");
                }

                String script = ConversionData.conversionHelpers.stream()
                        .filter(helper -> helper.getInputFormat().equals(inputFormat) && helper.getOutputFormat().equals(outputFormat))
                        .findFirst()
                        .map(ConversionHelper::getScript)
                        .orElseThrow(() -> new UnsupportedFormatException("Script not found for conversion from " + inputFormat + " to " + outputFormat));

                boolean success = runPythonScript(script, originalInputFile, originalOutputFile);
                if (!success) {
                    throw new PythonScriptExecutionException("Python script execution failed.");
                }

                return originalOutputFile;
            } catch (UnsupportedFormatException | PythonScriptExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private String getFileExtension(String file) {
        if (file.contains(".")) {
            return file.substring(file.lastIndexOf(".") + 1);
        }
        return "undefined";
    }

    private boolean runPythonScript(String scriptName, String inputFile, String outputFile) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "python3 " + ConversionData.pythonScriptsPath + "/" + scriptName + " " +
                ConversionData.inputPath + "/" + inputFile + " " +
                ConversionData.outputPath + "/" + outputFile;

        try {
            // Print the command
            System.out.println("Executing command: " + command);

            processBuilder.command(
                    "python3", ConversionData.pythonScriptsPath + "/" + scriptName,
                    ConversionData.inputPath + "/" + inputFile, ConversionData.outputPath + "/" + outputFile);

            processBuilder.redirectErrorStream(true); // Merge the error stream with the standard output stream

            Process process = processBuilder.start();

            // Read the output from the process (including errors)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                // Log or handle the error output
                System.err.println("Error executing Python script: " + output.toString());
                return false;
            }

            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Scheduled(fixedDelay = 60000)
    public void cleanupOldFiles() {
        cleanupDirectory(ConversionData.inputPath);
        cleanupDirectory(ConversionData.outputPath);
    }

    private void cleanupDirectory(String path) {
        File folder = new File(path);
        long cutoffTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.lastModified() < cutoffTime) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        // Log the successful deletion
                        System.out.println("Deleted expired file: " + file.getName());
                    } else {
                        // Log the failure to delete
                        System.err.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        }
    }
}
