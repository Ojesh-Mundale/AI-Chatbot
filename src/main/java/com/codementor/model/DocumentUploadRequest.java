package com.codementor.model;

import org.springframework.web.multipart.MultipartFile;

public class DocumentUploadRequest {
    private MultipartFile file;
    private String fileType;

    // Getters and Setters
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
