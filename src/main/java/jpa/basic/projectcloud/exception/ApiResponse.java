package jpa.basic.projectcloud.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ApiResponse<T>(
        boolean success,
        String code,
        T data,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(true, String.valueOf(status.value()), data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(HttpStatus status) {
        return new ApiResponse<>(true, String.valueOf(status.value()), null, LocalDateTime.now());
    }

    public static ApiResponse<ErrorResponse> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(),
                ErrorResponse.from(errorCode.getMessage()), LocalDateTime.now());
    }

    public static ApiResponse<ErrorResponse> fail(ErrorCode errorCode, List<FieldError> fieldErrors) {
        return new ApiResponse<>(false, errorCode.getCode(),
                ErrorResponse.of(errorCode.getMessage(), fieldErrors), LocalDateTime.now());
    }
}
