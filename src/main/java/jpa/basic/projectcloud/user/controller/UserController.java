package jpa.basic.projectcloud.user.controller;

import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("[API - LOG] 유저 생성 API 요청");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        log.info("[API - LOG] 유저 조회 API 요청");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userId));
    }
}
