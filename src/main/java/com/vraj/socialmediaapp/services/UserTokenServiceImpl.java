package com.vraj.socialmediaapp.services;

import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.helpers.EmailHelper;
import com.vraj.socialmediaapp.models.commons.EmailModel;
import com.vraj.socialmediaapp.models.commons.EmailTemplates;
import com.vraj.socialmediaapp.models.entities.User;
import com.vraj.socialmediaapp.models.entities.UserToken;
import com.vraj.socialmediaapp.models.entities.enums.TokenType;
import com.vraj.socialmediaapp.repositories.interfaces.UserRepository;
import com.vraj.socialmediaapp.repositories.interfaces.UserTokenRepository;
import com.vraj.socialmediaapp.services.interfaces.UserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository _userTokenRepository;
    private final UserRepository _userRepository;
    private final EmailHelper _emailHelper;
    private final EmailTemplates emailTemplates;
    @Value("${user.refresh_token_expire_in}")
    private long refreshTokenExpireIn;

    @Value("${user.token_expire_in}")
    private long tokenExpireIn;

    public UserTokenServiceImpl(UserTokenRepository userTokenRepository, UserRepository userRepository, EmailHelper emailHelper, EmailTemplates emailTemplates) {
        this._userTokenRepository = userTokenRepository;
        this._userRepository = userRepository;
        this._emailHelper = emailHelper;
        this.emailTemplates = emailTemplates;
    }

    @Override
    public UserToken generateRefreshToken(Long userId) {
        User user = _userRepository.findById(userId)
                .orElseThrow(
                        () -> new StatusException("Invalid user id.", HttpStatus.BAD_REQUEST)
                );
        UserToken userToken = new UserToken(
                UUID.randomUUID().toString(),
                TokenType.REFRESH_TOKEN,
                Date.from(Instant.now().plus(refreshTokenExpireIn, ChronoUnit.DAYS)),
                user
        );
        userToken = _userTokenRepository.save(userToken);
        return userToken;
    }

    @Override
    public boolean verifyRefreshToken(String token, Long userId) {
        Optional<UserToken> userToken = _userTokenRepository.findUserTokenByTokenAndUserId(token, userId);
        if (userToken.isEmpty()) {
            return false;
        }
        _userTokenRepository.delete(userToken.get());
        return true;
    }

    @Override
    public void generateEmailVerificationToken(Long id) {
        User user = _userRepository.findById(id).orElseThrow(
                () -> new StatusException("Invalid user id.", HttpStatus.BAD_REQUEST)
        );
        Date expireOn = Date.from(Instant.now().plus(tokenExpireIn, ChronoUnit.MINUTES));
        UserToken userToken = new UserToken(
                UUID.randomUUID().toString(),
                TokenType.EMAIL_VERIFICATION_TOKEN,
                expireOn,
                user
        );
        _userTokenRepository.save(userToken);
        String uri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/auth/verify-email")
                .queryParam("user_id", user.getId())
                .queryParam("token", userToken.getToken())
                .build().toUriString();
        EmailModel emailModel = new EmailModel();
        emailModel.setTemplateId(emailTemplates.getEmailVerification());
        emailModel.addTo(user.getEmail());
        emailModel.addValue("name", user.getName());
        emailModel.addValue("verify_email_url", uri);
        _emailHelper.sendEmail(emailModel);
    }

    @Override
    public boolean verifyEmailVerificationToken(String token, Long id) {
        Optional<UserToken> userToken = _userTokenRepository.findUserTokenByTokenAndUserId(token, id);
        if (userToken.isEmpty()) {
            return false;
        }
        _userTokenRepository.delete(userToken.get());
        return true;
    }

    @Override
    public boolean deleteRefreshToken(String token) {
        Optional<UserToken> userToken = _userTokenRepository.findByToken(token);
        if (userToken.isEmpty()) {
            return false;
        }
        _userTokenRepository.delete(userToken.get());
        return true;
    }

}
