package com.vraj.socialmediaapp.repositories.interfaces;

import com.vraj.socialmediaapp.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByNameIgnoreCase(String roleName);

    Optional<Role> findByNameIgnoreCase(String roleName);


    @Modifying(clearAutomatically = true)
    @Query("update Role r set r.isDeleted = true, r.lastModifiedOn=current_timestamp where r.id =?1")
    void deleteRoleById(Long roleId);

}
