package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.entity.CommentEntity;
import com.haile.springrestfulapi.entity.PostEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.CommentRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.CommentResponseDTO;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.CommentRepository;
import com.haile.springrestfulapi.repository.PostRepository;
import com.haile.springrestfulapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public CommentResponseDTO convertToDto(CommentEntity comment) {
        CommentResponseDTO.OutputUser user = new CommentResponseDTO.OutputUser(comment.getUser()
                                                                                      .getId(),
                                                                               comment.getUser()
                                                                                      .getUsername());

        CommentResponseDTO.OutputPost post = new CommentResponseDTO.OutputPost(comment.getPost()
                                                                                      .getId(),
                                                                               comment.getPost()
                                                                                      .getTitle(),
                                                                               comment.getPost()
                                                                                      .getContent());

        return CommentResponseDTO.builder()
                                 .id(comment.getId())
                                 .content(comment.getContent())
                                 .isApproved(comment.isApproved())
                                 .createdAt(comment.getCreatedAt())
                                 .updatedAt(comment.getUpdatedAt())
                                 .user(user)
                                 .post(post)
                                 .build();

    }

    public CommentEntity convertToEntity(CommentRequestDTO comment) {
        UserEntity user = userRepository.findById(comment.getUser()
                                                         .getId())
                                        .orElseThrow(() -> new ResourceNotFoundException("User with id  " + comment.getUser()
                                                                                                                   .getId() + " not found"));

        PostEntity post = postRepository.findById(comment.getPost()
                                                         .getId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Post with id  " + comment.getPost()
                                                                                                                   .getId() + " not found"));

        return CommentEntity.builder()
                            .content(comment.getContent())
                            .isApproved(comment.isApproved())
                            .user(user)
                            .post(post)
                            .build();
    }

    public CommentResponseDTO createNewComment(CommentRequestDTO comment) {
        CommentEntity commentEntity = convertToEntity(comment);
        return convertToDto(commentRepository.save(commentEntity));
    }


}

