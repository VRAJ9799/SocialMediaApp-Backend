package com.vraj.socialmediaapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private String id;

    private String caption;

    private Set<String> images = new HashSet<>();

    private UserDto user;

    private Set<Long> likedBy;

    @CreationTimestamp
    private Date createdOn;

    @LastModifiedDate
    private Date lastModifiedOn;

}
