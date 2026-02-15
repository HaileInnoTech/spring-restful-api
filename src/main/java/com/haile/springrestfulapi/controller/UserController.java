package com.haile.springrestfulapi.controller;


import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.UserRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.UserResponseDTO;
import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;


    // chỉ apply nơi khai báo @ExceptionHandler
    //    @ExceptionHandler(EntityNotFoundException.class)
    //    public ResponseEntity<ApiResponse<String>> entityNotFound(EntityNotFoundException e) {
    //            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    //    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createNewUser(@Valid @RequestBody UserEntity newUser) {
        UserResponseDTO user = this.userService.createNewUser(newUser);
        return ApiResponse.created(user);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAllUsers(@RequestParam(required = false) Integer page,
                                                                          @RequestParam(required = false) Integer size,
                                                                          @RequestParam(defaultValue = "id") String sort,
                                                                          @RequestParam(defaultValue = "asc") String direction,
                                                                          @RequestParam(required = false) String role) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);

        if (page == null || size == null) {
            List<UserResponseDTO> users;

            if (role == null) {
                users = userService.getAllUsers(Sort.by(sortDirection, sort));
            } else {
                users = userService.getAllUsers(role, Sort.by(sortDirection, sort));
            }

            return ApiResponse.success(new PageImpl<>(users));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<UserResponseDTO> users;
        if (role == null) {
            users = userService.getAllUsers(pageable);
        } else {
            users = userService.getAllUsers(role, pageable);
        }

        return ApiResponse.success(users);

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserEntity>> getUserById(@PathVariable Long id) {
        return ApiResponse.success(this.userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable Long id,
                                                          @Valid @RequestBody UserRequestDTO updateUser) {
        this.userService.updateUser(id, updateUser);
        return ApiResponse.success("Update successful");

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long id) {
        this.userService.deleteUserById(id);
        return ApiResponse.success("Delete successful");

    }
}
