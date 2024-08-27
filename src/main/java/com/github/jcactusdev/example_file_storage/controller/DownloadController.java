package com.github.jcactusdev.example_file_storage.controller;

import com.github.jcactusdev.example_file_storage.storage.FileStorageException;
import com.github.jcactusdev.example_file_storage.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class DownloadController {
    @Autowired
    private FileStorageService service;

    @GetMapping(value = "/{directory}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity getFile(@PathVariable String directory, @PathVariable String fileName) {
        if (service.notExists(directory, fileName)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        try {
            Resource resource = service.getFile(directory, fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8) + "\"")
                    .body(resource);
        } catch (FileStorageException e) {
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}
