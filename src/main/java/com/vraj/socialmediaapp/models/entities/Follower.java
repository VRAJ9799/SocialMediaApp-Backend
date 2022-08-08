package com.vraj.socialmediaapp.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "followers")
@IdClass(FollowerPkKey.class)
public class Follower {

    @Id
    @ManyToOne
    @JoinColumn(name = "from_user", foreignKey = @ForeignKey(name = "from_user_fk"))
    private User from;

    @Id
    @ManyToOne
    @JoinColumn(name = "to_user", foreignKey = @ForeignKey(name = "to_user_fk"))
    private User to;
}

