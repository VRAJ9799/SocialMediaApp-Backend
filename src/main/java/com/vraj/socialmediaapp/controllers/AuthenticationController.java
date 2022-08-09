package com.vraj.socialmediaapp.controllers;

import com.vraj.socialmediaapp.dtos.RefreshTokenRequestDto;
import com.vraj.socialmediaapp.dtos.SignInResponseDto;
import com.vraj.socialmediaapp.dtos.SignInUserDto;
import com.vraj.socialmediaapp.dtos.SignUpUserDto;
import com.vraj.socialmediaapp.helpers.Constants;
import com.vraj.socialmediaapp.helpers.CookieHelper;
import com.vraj.socialmediaapp.models.commons.ApiResponse;
import com.vraj.socialmediaapp.models.entities.UserToken;
import com.vraj.socialmediaapp.services.interfaces.AuthenticationService;
import com.vraj.socialmediaapp.services.interfaces.UserTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService _authenticationService;
    private final UserTokenService _userTokenService;
    private final CookieHelper _cookieHelper;

    public AuthenticationController(AuthenticationService _authenticationService, UserTokenService _userTokenService, CookieHelper _cookieHelper) {
        this._authenticationService = _authenticationService;
        this._userTokenService = _userTokenService;
        this._cookieHelper = _cookieHelper;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpUserDto signUpUserDto) {
        long userId = _authenticationService.registerUser(signUpUserDto);
        _userTokenService.generateEmailVerificationToken(userId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Account successfully created.");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInUserDto signInUserDto, HttpServletResponse httpServletResponse) {
        SignInResponseDto signInResponseDto = _authenticationService.loginUser(signInUserDto);
        UserToken userToken = _userTokenService.generateRefreshToken(signInResponseDto.getUser().getId());
        _cookieHelper.addCookie(httpServletResponse, Constants.REFRESH_TOKEN, userToken.getToken(), (int) (userToken.getExpireOn().getTime() / 1000));
        ApiResponse<SignInResponseDto> apiResponse = new ApiResponse<>(signInResponseDto);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/send-email-verification/{user_id}")
    public ResponseEntity<?> sendEmailVerification(@PathVariable(name = "user_id") Long userId) {
        _userTokenService.generateEmailVerificationToken(userId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Email sent successfully.");
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam(name = "user_id") Long userId, @RequestParam(name = "token") String token) {
        boolean isVerified = _userTokenService.verifyEmailVerificationToken(token, userId);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        if (isVerified) {
            apiResponse.setData("Email verified successfully.");
        } else {
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            apiResponse.setData("Invalid token.");
        }
        return ResponseEntity.status(apiResponse.getStatus())
                .body(apiResponse);
    }

    @PutMapping("/sign-out")
    public ResponseEntity<?> signOut(@CookieValue(name = Constants.REFRESH_TOKEN) String refreshToken) {
        boolean isSuccess = _userTokenService.deleteRefreshToken(refreshToken);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        if (isSuccess)
            apiResponse.setData("Log out success-full.");
        else
            apiResponse.setData("Error while logout.");
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = Constants.REFRESH_TOKEN) String refresh_token, @Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto, HttpServletResponse httpServletResponse) {
        boolean validRefreshToken = _userTokenService.verifyRefreshToken(refresh_token, refreshTokenRequestDto.getId());
        if (!validRefreshToken) {
            ApiResponse<String> apiResponse = new ApiResponse<>("Session expired.", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(apiResponse.getStatus())
                    .body(apiResponse);
        }
        UserToken userToken = _userTokenService.generateRefreshToken(refreshTokenRequestDto.getId());
        String accessToken = _authenticationService.generateAccessToken(refreshTokenRequestDto.getEmail());
        _cookieHelper.addCookie(httpServletResponse, Constants.REFRESH_TOKEN, userToken.getToken(), (int) (userToken.getExpireOn().getTime() / 1000));
        ApiResponse<String> apiResponse = new ApiResponse<>(accessToken);
        return ResponseEntity.ok(apiResponse);
    }
}
