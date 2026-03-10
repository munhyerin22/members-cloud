package jpa.basic.projectcloud.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

//    공통 에러 코드(A###)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "A001", "입력값이 올바르지 않습니다."),

//    사용자 관련 에러 코드(U###)
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "U001", "해당 유저는 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
