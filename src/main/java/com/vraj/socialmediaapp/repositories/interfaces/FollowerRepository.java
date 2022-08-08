package com.vraj.socialmediaapp.repositories.interfaces;

import com.vraj.socialmediaapp.models.entities.Follower;
import com.vraj.socialmediaapp.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface FollowerRepository extends JpaRepository<Follower, Long> {

    @Query("select f.from from Follower f where f.to.id =:id ")
    Set<User> getFollowers(Long id);

    @Query("select f.to from Follower f where f.from.id =:id ")
    Set<User> getFollowings(Long id);

}
