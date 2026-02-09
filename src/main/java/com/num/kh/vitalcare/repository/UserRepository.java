package com.num.kh.vitalcare.repository;

import com.num.kh.vitalcare.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsername(String username);

  Optional<UserEntity> findByUsernameOrEmail(String username, String email);

  Optional<UserEntity> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
