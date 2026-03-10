package jpa.basic.projectcloud.user.repository;

import jpa.basic.projectcloud.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
