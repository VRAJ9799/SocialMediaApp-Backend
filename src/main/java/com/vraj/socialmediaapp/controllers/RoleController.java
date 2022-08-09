package com.vraj.socialmediaapp.controllers;

import com.vraj.socialmediaapp.dtos.AddOrUpdateRole;
import com.vraj.socialmediaapp.helpers.ServiceHelper;
import com.vraj.socialmediaapp.models.commons.ApiResponse;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Role;
import com.vraj.socialmediaapp.services.interfaces.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService _roleService;

    public RoleController(RoleService roleService) {
        _roleService = roleService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllRoles(Paging<Role> rolePaging) {
        if (rolePaging == null) rolePaging = new Paging<>();
        Page<Role> roles = _roleService.getAllRoles(rolePaging);
        return ServiceHelper.getPagedResult(rolePaging, roles);
    }

    @GetMapping("/{role_id}")
    public ResponseEntity<?> getRoleById(@PathVariable("role_id") Long roleId) {
        Role role = _roleService.getRoleById(roleId);
        return ResponseEntity.ok(new ApiResponse<>(role));
    }

    @PostMapping("/")
    public ResponseEntity<?> createRole(@Valid @RequestBody AddOrUpdateRole addOrUpdateRole) {
        Role role = null;
        if (addOrUpdateRole.getId() == null) {
            role = _roleService.createRole(addOrUpdateRole);
        } else {
            role = _roleService.updateRole(addOrUpdateRole);
        }
        return ResponseEntity.ok(new ApiResponse<>(role));
    }

    @DeleteMapping("/{role_id}")
    public ResponseEntity<?> deleteRole(@PathVariable("role_id") Long roleId) {
        _roleService.deleteRole(roleId);
        return ResponseEntity.ok().build();
    }
}
