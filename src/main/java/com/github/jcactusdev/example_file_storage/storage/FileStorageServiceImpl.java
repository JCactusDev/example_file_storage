package com.github.jcactusdev.example_file_storage.storage;

import com.github.jcactusdev.example_file_storage.response.FileResponse;
import com.github.jcactusdev.example_file_storage.util.FileUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageProperties properties;

    private final Path rootLocation;

    private String urlDownload;

    public FileStorageServiceImpl(@Qualifier("fileStorageProperties") FileStorageProperties properties) {
        this.rootLocation = Paths.get(properties.getRoot());
        this.urlDownload = String.format("%s", properties.getUrlDownload());
        this.properties = properties;
    }

    @Override
    @PostConstruct
    public void init() {
        createDirectoryIsNotExists(rootLocation);
    }

    @Override
    public FileResponse upload(MultipartFile file) throws FileStorageException {
        String parentDirectory = FileUtils.generateRandomString(properties.getNewDirectoryLenght());
        Path directory = Paths.get(rootLocation.toString(), parentDirectory).normalize();
        createDirectoryIsNotExists(directory);

        String fileName = FileUtils.generateUniqueFileName(StringUtils.cleanPath(file.getOriginalFilename()));

        Path pathForSave = Paths.get(directory.toString(), fileName).normalize();
        if (Files.exists(pathForSave)) {
            throw new FileStorageException(String.format("Failed to store file %s (File already exists)", fileName));
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, pathForSave);
        } catch (IOException e) {
            throw new FileStorageException(String.format("Failed to store file %s (%s)", fileName, e.getMessage()), e);
        }

        return new FileResponse(fileName, parentDirectory, buildUri(parentDirectory, fileName), file.getContentType(), file.getSize());
    }

    @Override
    public List<FileResponse> uploadMultiple(MultipartFile[] files) throws FileStorageException {
        return Arrays.stream(files).map(this::upload).collect(Collectors.toList());
    }

    @Override
    public FileResponse get(String directory, String fileName) throws FileStorageException {
        Path filePath = Paths.get(rootLocation.toString(), directory, fileName).normalize();
        if (!Files.isReadable(filePath)) {
            throw new FileStorageException(String.format("File %s is not readable", fileName));
        }
        try {
            return new FileResponse(fileName, directory, buildUri(directory, fileName), Files.probeContentType(filePath), Files.size(filePath));
        } catch (IOException e) {
            throw new FileStorageException(String.format("Failed to get file info %s (%s)", fileName, e.getMessage()), e);
        }
    }

    @Override
    public List<FileResponse> getAll() throws FileStorageException {
        try {
            return Files.walk(rootLocation)
                    .filter(Files::isRegularFile)
                    .map(path -> get(path.getParent().getFileName().toString(), path.getFileName().getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileStorageException(String.format("Failed to get files info (%s)", e.getMessage()), e);
        }
    }

    @Override
    public Resource getFile(String directory, String fileName) throws FileStorageException {
        Path filePath = Paths.get(rootLocation.toString(), directory, fileName).normalize();
        if (!Files.isReadable(filePath)) {
            throw new FileStorageException(String.format("File %s is not readable", fileName));
        }
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new FileStorageException(String.format("Failed to load file %s", fileName), e);
        }
    }

    @Override
    public void delete(String directory, String fileName) throws FileStorageException {
        Path filePath = Paths.get(rootLocation.toString(), directory, fileName).normalize();
        if (!Files.isWritable(rootLocation.resolve(filePath))) {
            throw new FileStorageException(String.format("File %s is not readable", fileName));
        }
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new FileStorageException(String.format("Failed to delete file %s (%s)", fileName, e.getMessage()), e);
        }
    }

    @Override
    public void deleteAll() throws FileStorageException {
        try {
            if (Files.list(rootLocation).findAny().isEmpty()) {
                throw new FileStorageException("Failed to clear storage (Storage is empty)");
            }
            FileUtils.cleanDirectory(new File(rootLocation.toFile().getAbsolutePath()));
        } catch (IOException e) {
            throw new FileStorageException(String.format("Failed to clear storage (%s)", e.getMessage()), e);
        }
    }

    @Override
    public boolean notExists(String directory, String fileName) {
        return Files.notExists(Paths.get(rootLocation.toString(), directory, fileName).normalize());
    }

    @Override
    public boolean isAllowType(String type) {
        List<String> types = properties.getTypes();
        return types == null || types.isEmpty() || types.contains(type);
    }

    private void createDirectoryIsNotExists(Path pathDirectory) throws FileStorageException {
        try {
            if (Files.notExists(pathDirectory)) {
                Files.createDirectories(pathDirectory);
            }
        } catch (IOException e) {
            throw new FileStorageException(String.format("Failed to store directory  %s (%s)", pathDirectory, e.getMessage()), e);
        }
    }

    public String buildUri(String directory, String fileName) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .pathSegment(urlDownload, directory, fileName)
                .build()
                .toUriString();
    }

}