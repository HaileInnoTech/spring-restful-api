package com.haile.springrestfulapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tag không được để trống")
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<PostEntity> posts;

    public TagEntity(@NotNull(message = "tag.id không được để trống") Long id, @NotBlank(message = "tag.name không được để trống") String name) {
    }
}
