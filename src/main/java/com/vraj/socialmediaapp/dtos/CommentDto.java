package com.vraj.socialmediaapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String id;

    private String description;

    private UserDto user;

    @CreationTimestamp
    private Date createdOn;

    @LastModifiedDate
    private Date lastModifiedOn;
}

