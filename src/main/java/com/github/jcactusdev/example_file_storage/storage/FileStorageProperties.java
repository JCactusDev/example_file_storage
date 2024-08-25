package com.github.jcactusdev.example_file_storage.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "storage")
public class FileStorageProperties {
    private String root;
    private String urlDownload = "/download";
    private List<String> types;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String pathDownload) {
        this.urlDownload = pathDownload;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}