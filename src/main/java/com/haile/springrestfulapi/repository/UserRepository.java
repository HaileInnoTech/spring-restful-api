package com.haile.springrestfulapi.repository;

import com.haile.springrestfulapi.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity save(UserEntity user);

    Page<UserEntity> findAll(Pageable pageable);

    Optional<UserEntity> findById(Long id);

    void deleteById(Long id);

    Boolean existsByEmail(String email);

    List<UserEntity> findByRole_Name(String role_name, Sort sort);

    Page<UserEntity> findByRole_Name(String role_name, Pageable pageable);
}
