package com.haile.springrestfulapi.controller;

import com.haile.springrestfulapi.entity.RoleEntity;
import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RoleEntity>> createNewRole(@Valid @RequestBody RoleEntity role) {
        return ApiResponse.created(roleService.createNewRole(role));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<Page<RoleEntity>>> getAllRoles(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(defaultValue = "id") String sort, @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);

        if (page == null || size == null) {
            List<RoleEntity> roles = roleService.findAll(Sort.by(sortDirection, sort));
            Page<RoleEntity> pageResponse = new PageImpl<>(roles);
            return ApiResponse.success(pageResponse);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<RoleEntity> roles = roleService.findAll(pageable);

        return ApiResponse.success(roles);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RoleEntity>> getRoleById(@PathVariable Long id) {
        return ApiResponse.success(this.roleService.findById(id));
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<String>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleEntity newRole) {
        newRole.setId(id);
        this.roleService.updateRole(newRole);
        return ApiResponse.success("Update successful");

    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable Long id) {
        this.roleService.deleteById(id);
        return ApiResponse.success("Delete successful");

    }

}
