package com.haile.springrestfulapi.controller;

import com.haile.springrestfulapi.entity.dto.request.CommentRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.CommentResponseDTO;
import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "APIs for comment")
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createNewComment(@Valid @RequestBody CommentRequestDTO comment) {
        return ApiResponse.created(commentService.createNewComment(comment));

    }

    //    @GetMapping("/comments")
    //    public ResponseEntity<ApiResponse<CommentResponseDTO>> getAllComments() {
    //
    //    }


}
