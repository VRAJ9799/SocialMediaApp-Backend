package com.vraj.socialmediaapp.services;

import com.vraj.socialmediaapp.dtos.AddOrUpdateRole;
import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.helpers.Constants;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Role;
import com.vraj.socialmediaapp.repositories.interfaces.RoleRepository;
import com.vraj.socialmediaapp.services.interfaces.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository _roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        _roleRepository = roleRepository;
    }


    @Override
    public Page<Role> getAllRoles(Paging<Role> rolePaging) {
        PageRequest pageRequest = PageRequest.of(rolePaging.getPageNo(), rolePaging.getLimit(), Sort.by(rolePaging.getSortOrder(), rolePaging.getSortBy()));
        Page<Role> roles = _roleRepository.findAll(pageRequest);
        return roles;
    }

    @Override
    public Role getRoleById(Long roleId) {
        log.info("Getting role by id {}", roleId);
        Role role = _roleRepository.findById(roleId).orElseThrow(
                () -> new StatusException("Invalid role id.", HttpStatus.BAD_REQUEST)
        );
        return role;
    }

    @Override
    public Role createRole(AddOrUpdateRole addOrUpdateRole) {
        String roleName = "ROLE_" + addOrUpdateRole.getName().toUpperCase();
        boolean roleExists = _roleRepository.existsByNameIgnoreCase(roleName);
        if (roleExists) {
            log.error("Role with name {} already exists.", roleName);
            throw new RuntimeException("Role already exists.");
        }
        Role role = new Role(roleName);
        role = _roleRepository.save(role);
        return role;
    }

    @Override
    public Role updateRole(AddOrUpdateRole addOrUpdateRole) {
        String roleName = Constants.ROLE_PREFIX + addOrUpdateRole.getName().toUpperCase();
        Role role = _roleRepository.findById(addOrUpdateRole.getId()).orElseThrow(
                () -> new StatusException("Invalid role id.", HttpStatus.BAD_REQUEST)
        );
        boolean roleExists = _roleRepository.existsByNameIgnoreCase(roleName);
        if (roleExists) {
            throw new StatusException("Role already exists " + roleName + " .", HttpStatus.BAD_REQUEST);
        }
        role.setName(roleName);
        role = _roleRepository.save(role);
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        log.info("Deleting role with id {}.", roleId);
        _roleRepository.deleteRoleById(roleId);
    }
}
