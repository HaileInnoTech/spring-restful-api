package com.haile.springrestfulapi.repository;

import com.haile.springrestfulapi.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
