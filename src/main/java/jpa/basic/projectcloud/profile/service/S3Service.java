package jpa.basic.projectcloud.profile.service;

import io.awspring.cloud.s3.S3Template;
import jpa.basic.projectcloud.exception.CustomException;
import jpa.basic.projectcloud.exception.ErrorCode;
import jpa.basic.projectcloud.user.entity.User;
import jpa.basic.projectcloud.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class S3Service {

    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofDays(7);

    private final S3Template s3Template;
    private final UserRepository userRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 업로드 기능 구현
    @Transactional
    public String upload(Long id, MultipartFile file) {
        // user 여부 확인 후 id 없으면 Not found user 에러발생
        User user = userRepository.findById(id).orElseThrow(()
                -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // upload 실패 시 에러 발생
        try {
            //uuid로 한 사람이 여러 사진을 업로드 해도 경로 겹치지 않게 생성.
            String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            user.uploadKey(key);
            s3Template.upload(bucket, key, file.getInputStream());
            return key;
        } catch (IOException e) {
            //log.error("[API - LOG] 예외 발생");
            throw new CustomException(ErrorCode.UPLOAD_FAIL);
        }
    }

    // download 기능 구현 db에 이미지 파일 경로 저장
    public URL getDownloadUrl(Long id) {
        // userid 확인
        User user = userRepository.findById(id).orElseThrow(()
                -> new CustomException(ErrorCode.NOT_FOUND_USER));
        String key;
        // 프로필 이미지를 업로드한적이 없으면 프로필 없음 오류 발생
        try{
            key = user.getImageUrl();
        } catch (NullPointerException e){
            throw new CustomException(ErrorCode.NOT_FOUND_PROFILE);
        }

        // key에 저장된 경로를 따라 이미지 다운로드
        return s3Template.createSignedGetURL(bucket, key, PRESIGNED_URL_EXPIRATION);
    }
}
