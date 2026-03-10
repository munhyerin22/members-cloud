package jpa.basic.projectcloud.user.dto.response;

import jpa.basic.projectcloud.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String name,
        int age,
        String mbti
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .mbti(user.getMbti())
                .build();
    }
}
