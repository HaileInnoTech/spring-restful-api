package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.entity.RoleEntity;
import com.haile.springrestfulapi.helper.exception.ResourceAlreadyExistsException;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleEntity createNewRole(RoleEntity role) {

        Boolean nameExist = roleRepository.existsByName(role.getName());
        if (nameExist) {
            throw new ResourceAlreadyExistsException("Role with name " + role.getName() + " already exists");
        }
        return roleRepository.save(role);
    }

    public List<RoleEntity> findAll(Sort sort) {
        return roleRepository.findAll(sort);
    }

    public Page<RoleEntity> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public RoleEntity findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role with id = " + id + " not exists")
                );
    }

    public void deleteById(Long id) {
        this.findById(id);
        roleRepository.deleteById(id);
    }

    public void updateRole(RoleEntity role) {
        RoleEntity roleInDb = roleRepository.findById(role.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role with id = " + role.getId() + " not exists")
                );

        Boolean nameExist = roleRepository.existsByNameAndIdNot(role.getName(), role.getId());
        if (nameExist) {
            throw new ResourceAlreadyExistsException("Role with name " + role.getName() + " already exists");
        }
        roleInDb.setName(role.getName());
        roleInDb.setDescription(role.getDescription());
        roleRepository.save(roleInDb);


    }

}
