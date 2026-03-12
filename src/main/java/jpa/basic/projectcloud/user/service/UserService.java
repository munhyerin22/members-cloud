package jpa.basic.projectcloud.user.service;

import jpa.basic.projectcloud.exception.CustomException;
import jpa.basic.projectcloud.exception.ErrorCode;
import jpa.basic.projectcloud.user.dto.request.CreateUserRequest;
import jpa.basic.projectcloud.user.dto.response.UserResponse;
import jpa.basic.projectcloud.user.entity.User;
import jpa.basic.projectcloud.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // user 정보 저장(생성)
    @Transactional // 트러블 슈팅 : readOnly로 설정 후 해당 어노테이션 추가 안해서 save못하는 오류 발생 했었음.
    public UserResponse save(CreateUserRequest request) {
        User user = new User(request.name(), request.age(), request.mbti());

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    // user 조회 기능
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return UserResponse.from(user);
    }
}
