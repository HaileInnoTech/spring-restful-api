package com.haile.springrestfulapi.controller;

import com.haile.springrestfulapi.entity.dto.request.PostRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.PostResponseDTO;
import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post", description = "APIs for post")

@RestController
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostResponseDTO>> createNewPost(@Valid @RequestBody PostRequestDTO post) {
        return ApiResponse.created(postService.createNewPost(post));
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostResponseDTO>>> getAllPosts(@RequestParam(required = false) Integer page,
                                                                          @RequestParam(required = false) Integer size,
                                                                          @RequestParam(defaultValue = "id") String sort,
                                                                          @RequestParam(defaultValue = "asc") String direction,
                                                                          @RequestParam(required = false) String role) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);

        if (page == null || size == null) {
            List<PostResponseDTO> posts = postService.getAllPosts(Sort.by(sortDirection, sort));

            return ApiResponse.success(new PageImpl<>(posts));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<PostResponseDTO> posts = postService.getAllPosts(pageable);


        return ApiResponse.success(posts);

    }

}
