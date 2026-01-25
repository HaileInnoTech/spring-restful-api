package com.haile.springrestfulapi.controller;

import com.haile.springrestfulapi.entity.TagEntity;
import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping("/tags")
    public ResponseEntity<ApiResponse<TagEntity>> createNewTag(@RequestBody TagEntity tagEntity) {
        return ApiResponse.created(tagService.createNewTag(tagEntity));
    }

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<Page<TagEntity>>> getAllTags(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(defaultValue = "id") String sort, @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);

        if (page == null || size == null) {
            List<TagEntity> tags = tagService.findAllTags(Sort.by(sortDirection, sort));
            Page<TagEntity> pageResponse = new PageImpl<>(tags);
            return ApiResponse.success(pageResponse);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<TagEntity> tags = tagService.findAllTags(pageable);

        return ApiResponse.success(tags);
    }

    @GetMapping("/tags/{id}")
    public ResponseEntity<ApiResponse<TagEntity>> getTagById(@PathVariable Long id) {
        return ApiResponse.success(this.tagService.findById(id));
    }

    @PutMapping("/tags/{id}")
    public ResponseEntity<ApiResponse<String>> updateTag(@PathVariable Long id, @Valid @RequestBody TagEntity newTag) {
        newTag.setId(id);
        this.tagService.updateTag(newTag);
        return ApiResponse.success("Update successful");

    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTag(@PathVariable Long id) {
        this.tagService.deleteTag(id);
        return ApiResponse.success("Delete successful");

    }

}
