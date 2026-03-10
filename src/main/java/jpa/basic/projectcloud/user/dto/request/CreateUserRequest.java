package jpa.basic.projectcloud.user.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "나이는 필수입니다.")
        @Min(value = 1, message = "나이는 1살 이상이어야 합니다.")
        int age,

        @NotBlank(message = "mbti는 필수입니다.")
        String mbti
) {
}
