package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.entity.PostEntity;
import com.haile.springrestfulapi.entity.TagEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.PostRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.PostResponseDTO;
import com.haile.springrestfulapi.helper.SecurityUtil;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.PostRepository;
import com.haile.springrestfulapi.repository.TagRepository;
import com.haile.springrestfulapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    public PostResponseDTO convertPostToDto(PostEntity post) {

        List<PostResponseDTO.OutputTag> tags =
                post.getTags() == null
                        ? List.of()
                        : post.getTags()
                        .stream()
                        .map(tag -> new PostResponseDTO.OutputTag(
                                tag.getId(),
                                tag.getName()
                        ))
                        .toList();

        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(tags)
                .build();
    }

    /* =========================
       PostRequestDTO -> PostEntity
       ========================= */
    public PostEntity convertDtoToPost(
            PostRequestDTO dto,
            UserEntity user,
            List<TagEntity> tags
    ) {

        return PostEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .tags(tags)
                .build();
    }

    public PostResponseDTO createNewPost(PostRequestDTO dto) {

        Long u = SecurityUtil.getCurrentIdLogin()
                .get();
        // 1. Lấy user từ DB
        UserEntity user = userRepository.findById(u)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id = " + dto.getUser()
                                .getId())
                );


        // 2. Lấy tags từ DB
        List<TagEntity> tags = dto.getTags() == null
                ? List.of()
                : tagRepository.findAllById(
                dto.getTags()
                        .stream()
                        .map(PostRequestDTO.InputTag::getId)
                        .toList()
        );

        PostEntity postEntity = convertDtoToPost(dto, user, tags);

        PostEntity savedPost = postRepository.save(postEntity);

        return convertPostToDto(savedPost);
    }


    public List<PostResponseDTO> getAllPosts(Sort sort) {
        return postRepository.findAll(sort)
                .stream()
                .map(this::convertPostToDto)
                .collect(Collectors.toList());
    }

    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::convertPostToDto);
    }


    //delete Post
    public void deletePost(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id = " + id));
        postRepository.delete(post);
    }


}
