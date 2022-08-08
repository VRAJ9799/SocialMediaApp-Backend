package com.vraj.socialmediaapp.services.interfaces;

import com.vraj.socialmediaapp.dtos.SignInResponseDto;
import com.vraj.socialmediaapp.dtos.SignInUserDto;
import com.vraj.socialmediaapp.dtos.SignUpUserDto;

public interface AuthenticationService {
    long registerUser(SignUpUserDto signUpUserDto);

    SignInResponseDto loginUser(SignInUserDto signInUserDto);

    String generateAccessToken(String email);
}
