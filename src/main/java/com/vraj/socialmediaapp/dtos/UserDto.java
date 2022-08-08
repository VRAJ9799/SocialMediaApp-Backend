package com.vraj.socialmediaapp.dtos;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.vraj.socialmediaapp.models.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String username;

    private String email;

    @JsonIncludeProperties(value = {"id", "name"})
    private Role role;

    private boolean isLockedOut;
    private int lockedOutAttempt;

    private boolean isEmailVerified;

    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedOn;


}
