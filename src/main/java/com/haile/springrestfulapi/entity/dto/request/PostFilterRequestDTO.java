package com.haile.springrestfulapi.entity.dto.request;

import com.haile.springrestfulapi.entity.TagEntity;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostFilterRequestDTO {
    private String title;

    private List<String> tags;

    private Long userId;

    private Instant createdAt;

    private Instant updatedAt;
}
