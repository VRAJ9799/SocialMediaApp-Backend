package com.vraj.socialmediaapp.controllers;

import com.vraj.socialmediaapp.dtos.ChangePasswordDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.helpers.ServiceHelper;
import com.vraj.socialmediaapp.models.commons.ApiResponse;
import com.vraj.socialmediaapp.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService _userService;

    public UserController(UserService userService) {
        _userService = userService;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getUserById(@PathVariable(name = "user_id") Long userId) {
        UserDto user = _userService.getUserById(userId);
        ApiResponse<UserDto> apiResponse = new ApiResponse<>(user);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        _userService.changePassword(changePasswordDto);
        ApiResponse<?> apiResponse = new ApiResponse<>("Password changed successfully.");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-followers")
    public ResponseEntity<?> getFollowers() {
        Long curr_user = ServiceHelper.getLoggedInUser().getId();
        Set<UserDto> followers = _userService.getFollowers(curr_user);
        ApiResponse<Set<UserDto>> apiResponse = new ApiResponse<>(followers);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-followings")
    public ResponseEntity<?> getFollowings() {
        Long curr_user = ServiceHelper.getLoggedInUser().getId();
        Set<UserDto> followings = _userService.getFollowings(curr_user);
        ApiResponse<Set<UserDto>> apiResponse = new ApiResponse<>(followings);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteUserById(@PathVariable(name = "user_id") Long userId) {
        _userService.deleteUserById(userId);
        ApiResponse<String> apiResponse = new ApiResponse<>("User deleted successfully.");
        return ResponseEntity.ok(apiResponse);
    }
}
