package com.github.jcactusdev.example_file_storage.storage;

import com.github.jcactusdev.example_file_storage.response.FileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    void init();

    FileResponse upload(MultipartFile file);

    List<FileResponse> uploadMultiple(MultipartFile[] files);

    FileResponse get(String fileName);

    List<FileResponse> getAll();

    Resource getFile(String fileName);

    void delete(String fileName);

    void deleteAll();

    boolean exists(String fileName);

    boolean notExists(String fileName);

    boolean isAllowType(String type);
}