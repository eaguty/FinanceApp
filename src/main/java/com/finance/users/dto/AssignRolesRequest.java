package com.finance.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record AssignRolesRequest(@NotEmpty Set<@NotBlank String> roles) {}
