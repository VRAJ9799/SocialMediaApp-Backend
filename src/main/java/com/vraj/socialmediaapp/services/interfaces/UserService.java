package com.vraj.socialmediaapp.services.interfaces;

import com.vraj.socialmediaapp.dtos.ChangePasswordDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.User;
import com.vraj.socialmediaapp.models.entities.enums.ActivityType;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface UserService {

    Page<UserDto> getAllUsers(Paging<UserDto> userPaging);

    UserDto getUserById(Long id);

    UserDto getUserByEmail(String email);

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void followUnFollowUser(ActivityType activityType, Long follower_id);

    Set<UserDto> getFollowers(Long userId);

    Set<UserDto> getFollowings(Long userId);

    String generateAccessToken(String email);

    void emailVerification(boolean isEmailVerified, Long id);

    void changePassword(ChangePasswordDto changePasswordDto);

    void updateLockedOutAttempt(int attempt, String email, boolean isLockedOut);

    void deleteUserById(Long id);
}
