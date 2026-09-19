package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.healthcarequalite.dto.user.UserGetDTO;
import org.example.healthcarequalite.dto.user.UserRoleDTO;
import org.example.healthcarequalite.dto.user.UserUpdateDTO;
import org.example.healthcarequalite.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserGetDTO>> findAll( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page doit être positif ou égal à zéro");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("La taille de la page doit être comprise entre 1 et 100");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserGetDTO> users = userService.findAll(pageable);

        return ResponseEntity.ok(users);
    }



    @GetMapping("/{id}")
    public ResponseEntity<UserGetDTO> findById(@PathVariable Long id) {
        UserGetDTO user = userService.findById(id);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGetDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserGetDTO updatedUser = userService.update(id, userUpdateDTO);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserGetDTO> updateRole(@PathVariable Long id, @Valid @RequestBody UserRoleDTO userRoleDTO) {

        UserGetDTO updatedUser = userService.updateRole(id, userRoleDTO);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserGetDTO> getMyProfile( Authentication authentication) {

        String connectedEmail = authentication.getName();

        UserGetDTO user = userService.getMyProfile(connectedEmail);

        return ResponseEntity.ok(user);
    }

}