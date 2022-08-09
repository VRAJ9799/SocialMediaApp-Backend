package com.vraj.socialmediaapp.dtos;

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

    private long role;

    private boolean isLockedOut;

    private int lockedOutAttempt;

    private boolean isEmailVerified;

    //private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedOn;


}
