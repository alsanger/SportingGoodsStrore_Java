package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsById(Long id);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    User findByLogin(String login);
}
