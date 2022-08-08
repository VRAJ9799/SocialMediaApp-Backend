package com.vraj.socialmediaapp.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @NotBlank(message = "Description is required.")
    private String description;

    @DBRef(lazy = true)
    private Post post;

    @Field(name = "user_id")
    private Long userId;

    @Field(name = "deleted")
    private boolean isDeleted;

    @CreationTimestamp
    private Date createdOn;

    @LastModifiedDate
    private Date lastModifiedOn;

    public Comment(String description, Post post, Long userId) {
        this.description = description;
        this.post = post;
        this.userId = userId;
    }
}
