package jpa.basic.projectcloud.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    int age;
    @Column(nullable = false)
    String mbti;

    String imageUrl;

    @Builder
    public User(String name, int age, String mbti) {
        this.name = name;
        this.age = age;
        this.mbti = mbti;
    }

    // user처음 생성 시 프로필사진이 존재 하지 않아 따로 생성
    public void uploadKey(String imageUrl){
        this.imageUrl = imageUrl;
    }
}

