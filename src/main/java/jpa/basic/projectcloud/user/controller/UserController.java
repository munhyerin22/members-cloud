package jpa.basic.projectcloud.user.controller;

import jakarta.validation.Valid;
import jpa.basic.projectcloud.exception.ApiResponse;
import jpa.basic.projectcloud.user.dto.request.CreateUserRequest;
import jpa.basic.projectcloud.user.dto.response.UserResponse;
import jpa.basic.projectcloud.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // user 생성
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("[API - LOG] {}",request.name()); // lv1
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, userService.save(request)));
    }

    // user 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId) {
        log.info("[API - LOG] 조회 요청"); // lv1
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, userService.getUser(userId)));
    }
}
