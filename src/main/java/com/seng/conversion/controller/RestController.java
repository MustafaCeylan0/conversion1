package com.seng.conversion.controller;

import com.seng.conversion.converter.Converter;
import com.seng.conversion.helper.ConversionData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@org.springframework.web.bind.annotation.RestController
public class RestController{

    private final Converter converter;

    public RestController(Converter converter) {
        this.converter = converter;
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("format") String desiredFormat) {
        try {
            String originalFilename = file.getOriginalFilename();
            String uid = UUID.randomUUID().toString().replace("-","s");
            String modifiedInputFilename = appendUidToFileName(originalFilename, uid);
            String modifiedOutputFilename = changeFileExtension(modifiedInputFilename, desiredFormat);

            Path targetLocation = Paths.get(ConversionData.inputPath).resolve(modifiedInputFilename);
            Files.copy(file.getInputStream(), targetLocation);

            String outputFilePath = converter.convertFile(modifiedInputFilename, modifiedOutputFilename);
            if (outputFilePath == null) {
                throw new IOException("File conversion failed.");
            }

            String downloadLink = "http://localhost:8080/download/" + modifiedOutputFilename;
            //return ResponseEntity.ok("File converted successfully. Download at: " + downloadLink);
            return ResponseEntity.ok(downloadLink);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(ConversionData.outputPath).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String originalFilename = removeUidFromFilename(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String appendUidToFileName(String fileName, String uid) {
        String nameWithoutExtension = removeExtension(fileName);
        String extension = getFileExtension(fileName);
        return nameWithoutExtension + "-" + uid + (extension.isEmpty() ? "" : "." + extension);
    }

    private String removeExtension(String filename) {
        if (filename.lastIndexOf(".") > 0) {
            return filename.substring(0, filename.lastIndexOf('.'));
        }
        return filename;
    }

    private String getFileExtension(String filename) {
        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.') + 1);
        }
        return "";
    }

    private String changeFileExtension(String filename, String newExtension) {
        String nameWithoutExtension = removeExtension(filename);
        return nameWithoutExtension + "." + newExtension;
    }

    private String removeUidFromFilename(String filename) {
        // Extract the part before the UID
        int uidStart = filename.lastIndexOf('-');
        String nameWithoutUid = uidStart > 0 ? filename.substring(0, uidStart) : filename;

        // Extract the extension
        String extension = "";
        int extIndex = filename.lastIndexOf('.');
        if (extIndex > 0) {
            extension = filename.substring(extIndex);  // includes the dot
        }

        return nameWithoutUid + extension;
    }
}
