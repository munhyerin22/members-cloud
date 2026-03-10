package jpa.basic.projectcloud.user.dto.request;

public record CreateUserRequest(
        String name,
        int age,
        String mbti
) {
}
