package com.github.jcactusdev.example_file_storage.response;

public class FileResponse {
    private String fileName;
    private String uri;
    private String type;
    private long size;

    public FileResponse(String fileName, String uri, String type, long size) {
        this.fileName = fileName;
        this.uri = uri;
        this.type = type;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileResponse{" +
                "fileName='" + fileName + '\'' +
                ", uri='" + uri + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                '}';
    }
}