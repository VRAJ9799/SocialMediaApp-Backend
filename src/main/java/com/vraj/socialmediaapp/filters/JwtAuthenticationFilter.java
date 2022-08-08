package com.vraj.socialmediaapp.filters;

import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.helpers.Constants;
import com.vraj.socialmediaapp.helpers.CookieHelper;
import com.vraj.socialmediaapp.helpers.JwtHelper;
import com.vraj.socialmediaapp.models.entities.UserToken;
import com.vraj.socialmediaapp.services.interfaces.UserTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtHelper _jwtHelper;
    private final UserDetailsService _userDetailsService;
    private final UserTokenService _userTokenService;
    private final PathMatcher _pathMatcher;
    private final CookieHelper _cookieHelper;

    public JwtAuthenticationFilter(JwtHelper jwtHelper, UserDetailsService userDetailsService, UserTokenService userTokenService, PathMatcher pathMatcher, CookieHelper cookieHelper) {
        _jwtHelper = jwtHelper;
        _userDetailsService = userDetailsService;
        _userTokenService = userTokenService;
        _pathMatcher = pathMatcher;
        _cookieHelper = cookieHelper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(Constants.WHITELISTED_URLS).anyMatch(
                p -> _pathMatcher.match(p, request.getRequestURI())
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(Constants.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.info("Authentication header exists.");
            String token = authHeader.substring(7);
            String email = null;
            try {
                email = _jwtHelper.getEmailFromToken(token);
            } catch (ExpiredJwtException exception) {
                String refresh_token = _cookieHelper.getCookie(request, Constants.REFRESH_TOKEN);
                if (refresh_token == null)
                    throw new StatusException("You have to Login.", HttpStatus.UNAUTHORIZED);
                Claims claims = exception.getClaims();
                Long user_id = claims.get("user_id", Long.class);
                generateRefreshToken(response, refresh_token, user_id);
            } catch (Exception exception) {
                throw new StatusException("Invalid Token", HttpStatus.BAD_REQUEST);
            }
            if (email == null) filterChain.doFilter(request, response);
            UserDetails userDetails = _userDetailsService.loadUserByUsername(email);
            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("User with {} email login successful", email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void generateRefreshToken(HttpServletResponse httpServletResponse, String refresh_token, Long userId) {
        boolean validRefreshToken = _userTokenService.verifyRefreshToken(refresh_token, userId);
        if (!validRefreshToken)
            throw new StatusException("You have to Login.", HttpStatus.UNAUTHORIZED);
        UserToken userToken = _userTokenService.generateRefreshToken(userId);
        _cookieHelper.addCookie(httpServletResponse, Constants.REFRESH_TOKEN, userToken.getToken(), -1);
    }
}
