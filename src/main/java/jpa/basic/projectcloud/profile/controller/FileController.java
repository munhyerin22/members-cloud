package jpa.basic.projectcloud.profile.controller;

import jpa.basic.projectcloud.profile.dto.FileDownloadUrlResponse;
import jpa.basic.projectcloud.profile.dto.FileUploadResponse;
import jpa.basic.projectcloud.profile.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RestController
// upload, getDownloadUrl의 url이 같아서 하나로 작성
@RequestMapping("/api/members/{id}/profile-image")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;

    //이미지 업로드 기능 userid가 있어야 업로드 가능.
    @PostMapping
    public ResponseEntity<FileUploadResponse> upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String key = s3Service.upload(id ,file);
        return ResponseEntity.ok(new FileUploadResponse(key));
    }

    //userid로 이미지를 찾아서 다운로드
    @GetMapping
    public ResponseEntity<FileDownloadUrlResponse> getDownloadUrl(@PathVariable Long id) {
        URL url = s3Service.getDownloadUrl(id);
        return ResponseEntity.ok(new FileDownloadUrlResponse(url.toString()));
    }
}