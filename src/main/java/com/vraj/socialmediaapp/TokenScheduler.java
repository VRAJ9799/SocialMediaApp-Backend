package com.vraj.socialmediaapp;

import com.vraj.socialmediaapp.repositories.interfaces.UserTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Configuration
@EnableScheduling
public class TokenScheduler {

    private final UserTokenRepository _userTokenRepository;

    public TokenScheduler(UserTokenRepository userTokenRepository) {
        _userTokenRepository = userTokenRepository;
    }

    @Scheduled(cron = "@hourly")
    public void deleteExpiredTokens() {
        log.info("Running deleting expired token on {}.", Instant.now());
        Set<Long> expiredTokens = _userTokenRepository.findAllByExpired();

        log.info("Total deleted token found: {}.", expiredTokens.size());

        for (Long item : expiredTokens) {
            _userTokenRepository.deleteById(item);
        }
    }
}
