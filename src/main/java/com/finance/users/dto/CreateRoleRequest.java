package com.finanzas.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
        @NotBlank @Size(max = 50)
        @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]*$", message = "solo letras, números y guion bajo")
        String name,
        @Size(max = 255) String description
) {}
