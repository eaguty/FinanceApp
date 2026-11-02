package com.finanzas.users.controller;

import com.finanzas.users.dto.AssignRolesRequest;
import com.finanzas.users.dto.CreateUserRequest;
import com.finanzas.users.dto.UpdateUserRequest;
import com.finanzas.users.dto.UserResponse;
import com.finanzas.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/byname/{name}")
    public List<ResponseEntity<UserReponse>> findByName(@PathVariable String name){
        return userService.getAllUsersWithName(name);
    }

    @GetMapping
    public Page<UserResponse> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- roles del usuario ----------

    @PutMapping("/{id}/roles")
    public UserResponse replaceRoles(@PathVariable UUID id, @Valid @RequestBody AssignRolesRequest request) {
        return userService.replaceRoles(id, request.roles());
    }

    @PostMapping("/{id}/roles/{roleName}")
    public UserResponse addRole(@PathVariable UUID id, @PathVariable String roleName) {
        return userService.addRole(id, roleName);
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public UserResponse removeRole(@PathVariable UUID id, @PathVariable String roleName) {
        return userService.removeRole(id, roleName);
    }
}
