package com.github.jcactusdev.example_file_storage.controller;

import com.github.jcactusdev.example_file_storage.storage.FileStorageException;
import com.github.jcactusdev.example_file_storage.storage.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/storage")
public class FileStorageRestController {
    private final FileStorageService service;

    public FileStorageRestController(FileStorageService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        if (file == null
                || file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        if (!service.isAllowType(file.getContentType())) {
            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .build();
        }
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(service.upload(file));
        } catch (FileStorageException e) {
            return ResponseEntity
                    .status(HttpStatus.INSUFFICIENT_STORAGE)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/upload-multiple")
    @ResponseBody
    public ResponseEntity uploadMultiple(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        if (Arrays.stream(files).anyMatch(file -> !service.isAllowType(file.getContentType()))) {
            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .build();
        }
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(service.uploadMultiple(files));
        } catch (FileStorageException e) {
            return ResponseEntity
                    .status(HttpStatus.INSUFFICIENT_STORAGE)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/get/{directory}/{fileName:.+}")
    public ResponseEntity get(@PathVariable String directory, @PathVariable String fileName) {
        if (service.notExists(directory, fileName)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        try {
            return ResponseEntity.ok().body(service.get(directory, fileName));
        } catch (FileStorageException e) {
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity getAll() {
        try {
            return ResponseEntity.ok().body(service.getAll());
        } catch (FileStorageException e) {
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{directory}/{fileName:.+}")
    public ResponseEntity delete(@PathVariable String directory, @PathVariable String fileName) {
        if (service.notExists(directory, fileName)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        try {
            service.delete(directory, fileName);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (FileStorageException e) {
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity clear() {
        try {
            service.deleteAll();
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (FileStorageException e) {
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}