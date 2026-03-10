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

    @Builder
    public User(String name, int age, String mbti) {
        this.name = name;
        this.age = age;
        this.mbti = mbti;
    }
}

