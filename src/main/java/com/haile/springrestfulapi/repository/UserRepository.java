package com.haile.springrestfulapi.repository;

import com.haile.springrestfulapi.entity.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity save(UserEntity user);

    Page<UserEntity> findAll(Pageable pageable);

    //    @EntityGraph will join table Role when get user to solve n+1 query
    //    @EntityGraph(attributePaths = "role")
    //    Page<UserEntity> findAll(Specification<UserEntity> specs, Pageable pageable);


    Optional<UserEntity> findById(Long id);

    void deleteById(Long id);

    Boolean existsByEmail(String email);

    List<UserEntity> findByRole_Name(String role_name, Sort sort);

    Page<UserEntity> findByRole_Name(String role_name, Pageable pageable);

    List<UserEntity> email(String email);

    Optional<UserEntity> findByEmail(String email);
}
