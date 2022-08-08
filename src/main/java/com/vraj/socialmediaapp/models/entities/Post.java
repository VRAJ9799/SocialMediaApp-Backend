package com.vraj.socialmediaapp.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @NotBlank(message = "Caption is required.")
    private String caption;
    private Set<String> images = new HashSet<>();

    private Set<Long> likedBy = new HashSet<>();

    private Set<Long> savedBy = new HashSet<>();

    @Field(name = "user_id")
    private Long userId;

    @Field(name = "deleted")
    private boolean isDeleted;

    @CreationTimestamp
    private Date createdOn;

    @LastModifiedDate
    private Date lastModifiedOn;

    // Methods
    public void addImage(String image) {
        this.images.add(image);
    }

    public void removeImage(String image) {
        this.images.remove(image);
    }
}
