package com.finanzas.users.service;

import com.finanzas.users.dto.CreateUserRequest;
import com.finanzas.users.dto.UpdateUserRequest;
import com.finanzas.users.dto.UserResponse;
import com.finanzas.users.entity.Role;
import com.finanzas.users.entity.User;
import com.finanzas.users.exception.DuplicateResourceException;
import com.finanzas.users.exception.ResourceNotFoundException;
import com.finanzas.users.repository.RoleRepository;
import com.finanzas.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserResponse create(CreateUserRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Ya existe un usuario con el email " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());

        Set<String> roleNames = (request.roles() == null || request.roles().isEmpty())
                ? Set.of(DEFAULT_ROLE)
                : request.roles();
        user.setRoles(resolveRoles(roleNames));

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return UserResponse.from(findUser(id));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = findUser(id);
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Ya existe un usuario con el email " + email);
        }

        user.setEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        if (request.active() != null) {
            user.setActive(request.active());
        }
        return UserResponse.from(userRepository.save(user));
    }

    public void delete(UUID id) {
        userRepository.delete(findUser(id));
    }

    // ---------- roles ----------

    /** Reemplaza todos los roles del usuario. */
    public UserResponse replaceRoles(UUID id, Set<String> roleNames) {
        User user = findUser(id);
        user.setRoles(resolveRoles(roleNames));
        return UserResponse.from(user);
    }

    public UserResponse addRole(UUID id, String roleName) {
        User user = findUser(id);
        user.getRoles().addAll(resolveRoles(Set.of(roleName)));
        return UserResponse.from(user);
    }

    public UserResponse removeRole(UUID id, String roleName) {
        User user = findUser(id);
        String normalized = normalizeRole(roleName);
        user.getRoles().removeIf(role -> role.getName().equals(normalized));
        return UserResponse.from(user);
    }

    // ---------- helpers ----------

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        Set<String> normalized = roleNames.stream()
                .map(this::normalizeRole)
                .collect(Collectors.toSet());

        Set<Role> found = new HashSet<>(roleRepository.findByNameIn(normalized));
        if (found.size() != normalized.size()) {
            Set<String> missing = new HashSet<>(normalized);
            found.forEach(role -> missing.remove(role.getName()));
            throw new ResourceNotFoundException("Roles no encontrados: " + missing);
        }
        return found;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        return role.trim().toUpperCase(Locale.ROOT);
    }
}
