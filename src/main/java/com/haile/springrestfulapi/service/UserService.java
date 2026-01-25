package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.entity.RoleEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.UserRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.RoleResponseDTO;
import com.haile.springrestfulapi.entity.dto.response.UserResponseDTO;
import com.haile.springrestfulapi.helper.exception.ResourceAlreadyExistsException;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.RoleRepository;
import com.haile.springrestfulapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RoleResponseDTO convertToRoleResponseDTO(RoleEntity role) {
        return RoleResponseDTO
                .builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }

    public UserResponseDTO convertToUserResponseDTO(UserEntity userEntity) {
//        UserResponseDTO dto = new UserResponseDTO();
//
//        dto.setId(userEntity.getId());
//        dto.setUsername(userEntity.getUsername());
//        dto.setEmail(userEntity.getEmail());
//        dto.setAddress(userEntity.getAddress());
//        dto.setRole(userEntity.getRole());
//
//        return dto;
        return UserResponseDTO.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .address(userEntity.getAddress())
                .role(convertToRoleResponseDTO(userEntity.getRole()))
                .build();
    }

    public UserResponseDTO createNewUser(UserEntity newUser) {
        Boolean emailExists = userRepository.existsByEmail(newUser.getEmail());

        if (emailExists) {
            throw new ResourceAlreadyExistsException("Email already exists " + newUser.getEmail());
        }
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());

        newUser.setPassword(hashedPassword);

        Long roleId = newUser.getRole().getId();
        String roleName = newUser.getRole().getName();
        RoleEntity roleEntity = roleRepository.findAllByIdOrName(roleId, roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        newUser.setRole(roleEntity);

        return convertToUserResponseDTO(userRepository.save(newUser));
    }


    // Không phân trang
    public List<UserResponseDTO> convertToUserResponseDTO(List<UserEntity> users) {
        return users.stream()
                .map(this::convertToUserResponseDTO)
                .toList();
    }

    public List<UserResponseDTO> getAllUsers(Sort sort) {
        return this.convertToUserResponseDTO(userRepository.findAll(sort));
    }

    public List<UserResponseDTO> getAllUsers(String role, Sort sort) {
        return this.convertToUserResponseDTO(userRepository.findByRole_Name(role, sort));
    }


    // Có phân trang
    public Page<UserResponseDTO> convertToUserResponseDTO(Page<UserEntity> page) {
        return page.map(this::convertToUserResponseDTO);
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return this.convertToUserResponseDTO(userRepository.findAll(pageable));
    }

    public Page<UserResponseDTO> getAllUsers(String role, Pageable pageable) {
        return this.convertToUserResponseDTO(userRepository.findByRole_Name(role, pageable));
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with id = " + id)
                );
    }

    public void updateUser(Long id, UserRequestDTO user) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setAddress(user.getAddress());

        userRepository.save(entity);
    }


    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
