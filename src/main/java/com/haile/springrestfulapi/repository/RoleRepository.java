package com.haile.springrestfulapi.repository;

import com.haile.springrestfulapi.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Boolean existsByName(String name);

    Optional<RoleEntity> findByName(String name);

    void deleteById(Long id);

    Boolean existsByNameAndIdNot(String name, Long id);

    Optional<RoleEntity> findAllByIdOrName(Long id, String name);

    String findAByIdOrName(Long id, String name);
}
