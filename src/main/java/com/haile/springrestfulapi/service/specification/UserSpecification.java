package com.haile.springrestfulapi.service.specification;

import com.haile.springrestfulapi.entity.RoleEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.UserFilterRequestDTO;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<UserEntity> hasName(UserFilterRequestDTO userFilter) {
        return (root, query, cb) -> {
            if (userFilter.getName() == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("username"), userFilter.getName());
        };
    }


    public static Specification<UserEntity> hasEmail(UserFilterRequestDTO userFilter) {
        return (root, query, cb) -> {
            if (userFilter.getEmail() == null) {
                return cb.conjunction();
            }
            return cb.like(root.get("email"), userFilter.getEmail());
        };
    }

    public static Specification<UserEntity> hasAddress(UserFilterRequestDTO userFilter) {
        return (root, query, cb) -> {
            if (userFilter.getAddress() == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("address"), userFilter.getAddress());

        };
    }

    public static Specification<UserEntity> hasRole(UserFilterRequestDTO userFilter) {
        return (root, query, cb) -> {
            if (userFilter.getRoleName() == null) {
                return cb.conjunction();
            }
            Join<UserEntity, RoleEntity> roleJoin = root.join("roles", JoinType.INNER);

            return cb.equal(cb.lower(roleJoin.get("name")), userFilter.getRoleName());

        };
    }
}
