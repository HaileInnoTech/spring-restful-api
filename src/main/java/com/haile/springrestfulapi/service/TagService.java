package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.entity.TagEntity;
import com.haile.springrestfulapi.helper.exception.ResourceAlreadyExistsException;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public TagEntity createNewTag(TagEntity tagEntity) {
        tagRepository.findByName(tagEntity.getName())
                .ifPresent(tag -> {
                    throw new ResourceAlreadyExistsException(
                            "Tag đã tồn tại với name = " + tagEntity.getName()
                    );
                });

        return tagRepository.save(tagEntity);
    }

    public TagEntity findById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag không tồn tại với id = " + id));
    }


    public TagEntity updateTag(TagEntity tagEntity) {

        TagEntity tag = tagRepository.findById(tagEntity.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tag không tồn tại với id = " + tagEntity.getId()
                        )
                );

        if (tagRepository.existsByNameAndIdNot(
                tagEntity.getName(),
                tagEntity.getId()
        )) {
            throw new ResourceAlreadyExistsException(
                    "Tag đã tồn tại với name = " + tagEntity.getName()
            );
        }

        tag.setName(tagEntity.getName());
        return tagRepository.save(tag);
    }


    public List<TagEntity> findAllTags(Sort sort) {
        return tagRepository.findAll(sort);
    }

    public Page<TagEntity> findAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Tag không tồn tại với id = " + id
            );
        }
        tagRepository.deleteById(id);
    }
}

