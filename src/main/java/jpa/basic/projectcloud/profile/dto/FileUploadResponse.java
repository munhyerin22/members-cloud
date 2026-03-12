package jpa.basic.projectcloud.profile.dto;

import lombok.Getter;

@Getter
public class FileUploadResponse {

    private final String key;

    public FileUploadResponse(String key) {
        this.key = key;
    }
}
