package com.seng.conversion.converter;

import com.seng.conversion.helper.ConversionHelper;
import com.seng.conversion.helper.ConversionData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
public class Converter {

    public boolean isConversionPossible(String inputFormat, String outputFormat) {
        return ConversionData.conversionHelpers.stream()
                .anyMatch(helper -> helper.getInputFormat().equals(inputFormat) &&
                        helper.getOutputFormat().equals(outputFormat));
    }

    public String convertFile(String originalInputFile, String originalOutputFile) {
        try {
            String inputFormat = getFileExtension(originalInputFile);
            String outputFormat = getFileExtension(originalOutputFile);

            if (isConversionPossible(inputFormat, outputFormat)) {
                String script = ConversionData.conversionHelpers.stream()
                        .filter(helper -> helper.getInputFormat().equals(inputFormat) && helper.getOutputFormat().equals(outputFormat))
                        .findFirst()
                        .map(ConversionHelper::getScript)
                        .orElseThrow(() -> new IllegalArgumentException("Script not found for conversion from " + inputFormat + " to " + outputFormat));

                boolean success = runPythonScript(script, originalInputFile, originalOutputFile);

                if (success) {
                    return Paths.get(ConversionData.outputPath, originalOutputFile).toString();
                } else {
                    throw new IOException("Python script execution failed.");
                }
            } else {
                throw new IllegalArgumentException("Conversion from " + inputFormat + " to " + outputFormat + " is not supported.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileExtension(String file) {
        if (file.contains(".")) {
            return file.substring(file.lastIndexOf(".") + 1);
        }
        return "undefined";
    }

    private boolean runPythonScript(String scriptName, String inputFile, String outputFile) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            processBuilder.command("python3", ConversionData.pythonScriptsPath + "/" + scriptName,
                    ConversionData.inputPath + "/" + inputFile, ConversionData.outputPath + "/" + outputFile);
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Return true if the script executed successfully
            return exitCode == 0;
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
