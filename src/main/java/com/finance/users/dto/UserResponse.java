package com.finanzas.users.dto;

import com.finanzas.users.entity.Role;
import com.finanzas.users.entity.User;

import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User user) {
        Set<String> roleNames = new TreeSet<>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getName());
        }
        return new UserResponse(
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.isActive(), roleNames, user.getCreatedAt(), user.getUpdatedAt());
    }
}
