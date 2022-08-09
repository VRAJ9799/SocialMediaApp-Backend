package com.vraj.socialmediaapp.services.interfaces;

import com.vraj.socialmediaapp.models.entities.UserToken;

public interface UserTokenService {
    UserToken generateRefreshToken(Long userId);

    boolean verifyRefreshToken(String token, Long userId);

    void generateEmailVerificationToken(Long id);

    boolean verifyEmailVerificationToken(String token, Long id);

    boolean deleteRefreshToken(String token);
}
