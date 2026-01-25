package com.haile.springrestfulapi.repository;

import com.haile.springrestfulapi.entity.TagEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    @Override
    TagEntity save(TagEntity tagEntity);

    Page<TagEntity> findAll(@NonNull Pageable pageable);

    Optional<TagEntity> findByName(String name);

    void deleteById(Long id);

    Boolean existsByNameAndIdNot(String name, Long id);

}
