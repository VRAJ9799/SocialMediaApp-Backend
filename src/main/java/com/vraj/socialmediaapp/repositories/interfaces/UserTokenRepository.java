package com.vraj.socialmediaapp.repositories.interfaces;

import com.vraj.socialmediaapp.models.entities.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    @Query("select id from UserToken where expireOn < current_timestamp ")
    Set<Long> findAllByExpired();

    Optional<UserToken> findByToken(String token);

    Optional<UserToken> findUserTokenByTokenAndUserId(String token, Long userId);

}
