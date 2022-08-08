package com.vraj.socialmediaapp.services.interfaces;

import com.vraj.socialmediaapp.dtos.AddOrUpdateRole;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Role;
import org.springframework.data.domain.Page;

public interface RoleService {

    Page<Role> getAllRoles(Paging<Role> rolePaging);

    Role getRoleById(Long roleId);

    Role createRole(AddOrUpdateRole addOrUpdateRole);

    Role updateRole(AddOrUpdateRole addOrUpdateRole);

    void deleteRole(Long roleId);

}
