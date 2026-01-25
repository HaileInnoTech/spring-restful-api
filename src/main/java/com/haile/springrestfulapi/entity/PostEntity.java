package com.haile.springrestfulapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    private Instant createAt;

    private Instant updateAt;

    // foreign key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "post")
    private List<CommentEntity> comments;

    @ManyToMany
    @JoinTable(name = "post_tag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<TagEntity> tags;
}
