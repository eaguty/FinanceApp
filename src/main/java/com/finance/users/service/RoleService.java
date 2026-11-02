package com.finanzas.users.service;

import com.finanzas.users.dto.CreateRoleRequest;
import com.finanzas.users.dto.RoleResponse;
import com.finanzas.users.entity.Role;
import com.finanzas.users.exception.DuplicateResourceException;
import com.finanzas.users.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream().map(RoleResponse::from).toList();
    }

    public RoleResponse create(CreateRoleRequest request) {
        String name = request.name().trim().toUpperCase(Locale.ROOT);
        if (roleRepository.existsByName(name)) {
            throw new DuplicateResourceException("Ya existe el rol " + name);
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(request.description());
        return RoleResponse.from(roleRepository.save(role));
    }
}
