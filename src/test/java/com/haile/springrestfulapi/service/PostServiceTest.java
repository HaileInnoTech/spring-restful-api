package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.entity.PostEntity;
import com.haile.springrestfulapi.entity.TagEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.response.PostResponseDTO;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.PostRepository;
import com.haile.springrestfulapi.repository.TagRepository;
import com.haile.springrestfulapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository postRepository;
    @Mock UserRepository userRepository;
    @Mock TagRepository tagRepository;

    @InjectMocks PostService postService;

    private UserEntity mockUser;
    private TagEntity mockTag;
    private PostEntity mockPost;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder().id(1L).email("user@example.com").build();
        mockTag  = TagEntity.builder().id(1L).name("Java").build();
        mockPost = PostEntity.builder()
                .id(1L)
                .title("Hello Spring")
                .content("Content here")
                .user(mockUser)
                .tags(List.of(mockTag))
                .build();
    }

    // ─── convertPostToDto() ─────────────────────────────────────────────────

    @Test
    void convertPostToDto_mapsFieldsCorrectly() {
        PostResponseDTO dto = postService.convertPostToDto(mockPost);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Hello Spring");
        assertThat(dto.getContent()).isEqualTo("Content here");
        assertThat(dto.getTags()).hasSize(1);
        assertThat(dto.getTags().get(0).getName()).isEqualTo("Java");
    }

    @Test
    void convertPostToDto_withNullTags_returnsEmptyTagList() {
        mockPost.setTags(null);
        PostResponseDTO dto = postService.convertPostToDto(mockPost);

        assertThat(dto.getTags()).isEmpty();
    }

    // ─── getAllPosts() ───────────────────────────────────────────────────────

    @Test
    void getAllPosts_withSort_returnsListOfDtos() {
        when(postRepository.findAll(any(Sort.class))).thenReturn(List.of(mockPost));

        List<PostResponseDTO> result = postService.getAllPosts(Sort.by("id"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Hello Spring");
    }

    @Test
    void getAllPosts_withPageable_returnsPageOfDtos() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<PostEntity> page = new PageImpl<>(List.of(mockPost));
        when(postRepository.findAll(pageable)).thenReturn(page);

        Page<PostResponseDTO> result = postService.getAllPosts(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Hello Spring");
    }

    // ─── deletePost() ───────────────────────────────────────────────────────

    @Test
    void deletePost_withExistingId_deletesPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        postService.deletePost(1L);

        verify(postRepository, times(1)).delete(mockPost);
    }

    @Test
    void deletePost_withNonExistingId_throwsResourceNotFoundException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with id = 99");
    }
}
