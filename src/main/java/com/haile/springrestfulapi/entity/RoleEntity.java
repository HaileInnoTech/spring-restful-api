package com.haile.springrestfulapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity extends BaseEntity {
    @NotBlank
    private String name;

    private String description;

    @OneToMany(mappedBy = "role")
    private List<UserEntity> users;

}
