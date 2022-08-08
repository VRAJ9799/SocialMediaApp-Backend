package com.vraj.socialmediaapp.helpers;

import com.vraj.socialmediaapp.dtos.AddOrUpdateRole;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Role;
import com.vraj.socialmediaapp.services.interfaces.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitialData implements CommandLineRunner {

    private final RoleService _roleService;

    public InitialData(RoleService roleService) {
        _roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        insertRoles();
    }

    private void insertRoles() {
        Page<Role> roles = _roleService.getAllRoles(new Paging<Role>());
        if (roles.isEmpty()) {
            log.info("Seeding Roles...");
            _roleService.createRole(new AddOrUpdateRole(Constants.ROLE_USER));
            _roleService.createRole(new AddOrUpdateRole(Constants.ROLE_ADMIN));
        }
    }
}
