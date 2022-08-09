package com.vraj.socialmediaapp.services;

import com.vraj.socialmediaapp.dtos.SignInResponseDto;
import com.vraj.socialmediaapp.dtos.SignInUserDto;
import com.vraj.socialmediaapp.dtos.SignUpUserDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.models.AppUser;
import com.vraj.socialmediaapp.models.entities.User;
import com.vraj.socialmediaapp.services.interfaces.AuthenticationService;
import com.vraj.socialmediaapp.services.interfaces.RoleService;
import com.vraj.socialmediaapp.services.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService _userService;
    private final RoleService _roleService;
    private final PasswordEncoder _passwordEncoder;
    private final ModelMapper _modelMapper;
    private final AuthenticationManager _authManager;
    @Value("${user.default_role_id}")
    private long role_id;
    @Value("${user.locked_out_attempt}")
    private long lockedOutAttempt;

    public AuthenticationServiceImpl(UserService _userService, RoleService _roleService, PasswordEncoder _passwordEncoder, ModelMapper modelMapper, AuthenticationManager authManager) {
        this._userService = _userService;
        this._roleService = _roleService;
        this._passwordEncoder = _passwordEncoder;
        this._modelMapper = modelMapper;
        this._authManager = authManager;
    }

    @Override
    public long registerUser(SignUpUserDto signUpUserDto) {
        log.info("Registering user with email {}.", signUpUserDto.getEmail());
        boolean existsByEmail = _userService.existsByEmail(signUpUserDto.getEmail());
        if (existsByEmail) {
            throw new StatusException("Email already exist.", HttpStatus.BAD_REQUEST);
        }
        boolean existsByUsername = _userService.existsByUsername(signUpUserDto.getUsername());
        if (existsByUsername) {
            throw new StatusException("Username already exist.", HttpStatus.BAD_REQUEST);
        }
        User user = new User(
                signUpUserDto.getName(),
                signUpUserDto.getUsername(),
                signUpUserDto.getEmail().toLowerCase(),
                _passwordEncoder.encode(signUpUserDto.getPassword()),
                _roleService.getRoleById(role_id)
        );
        user = _userService.save(user);
        return user.getId();
    }

    @Override
    public SignInResponseDto loginUser(SignInUserDto signInUserDto) {
        log.info("Login user with email {}.", signInUserDto.getEmail());
        UserDetails userDetails = null;
        try {
            Authentication authentication = _authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInUserDto.getEmail(), signInUserDto.getPassword())
            );
            userDetails = (UserDetails) authentication.getPrincipal();
            _userService.updateLockedOutAttempt(0, userDetails.getUsername(), false);
        } catch (BadCredentialsException badCredentialsException) {
            if (userDetails == null) {
                throw new StatusException("Invalid Email or Password.", HttpStatus.BAD_REQUEST);
            }
            UserDto user = _userService.getUserByEmail(userDetails.getUsername());
            if (user.isLockedOut()) {
                throw new StatusException("Your account is locked because of wrong attempts. Please try again after some-time.", HttpStatus.BAD_REQUEST);
            }
            int attempts = user.getLockedOutAttempt();
            boolean isLockedOut = false;
            if (attempts == lockedOutAttempt - 1) {
                isLockedOut = true;
            }
            _userService.updateLockedOutAttempt(attempts + 1, userDetails.getUsername(), isLockedOut);
            throw new StatusException("Invalid Email or Password.", HttpStatus.BAD_REQUEST);
        } catch (DisabledException exception) {
            throw new StatusException("Your account is disabled.", HttpStatus.BAD_REQUEST);
        } catch (LockedException exception) {
            throw new StatusException("Your account is locked because of wrong attempts. Please try again after some-time.", HttpStatus.BAD_REQUEST);
        }
        String access_token = _userService.generateAccessToken(userDetails.getUsername());
        UserDto user = null;
        if (userDetails instanceof AppUser appUser) {
            user = _modelMapper.map(appUser.getUser(), UserDto.class);
        }
        SignInResponseDto signInResponseDto = new SignInResponseDto(
                user,
                access_token
        );
        return signInResponseDto;
    }

    @Override
    public String generateAccessToken(String email) {
        String accessToken = _userService.generateAccessToken(email);
        return accessToken;
    }


}
