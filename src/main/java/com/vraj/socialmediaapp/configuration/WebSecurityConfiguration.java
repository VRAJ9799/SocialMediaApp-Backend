package com.vraj.socialmediaapp.configuration;

import com.vraj.socialmediaapp.filters.JwtAuthenticationFilter;
import com.vraj.socialmediaapp.helpers.Constants;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Date;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final UserDetailsService _userDetailsService;
    private final PasswordEncoder _passwordEncoder;
    private final JwtAuthenticationFilter _jwtAuthenticationFilter;

    public WebSecurityConfiguration(UserDetailsService _userDetailsService, PasswordEncoder _passwordEncoder, JwtAuthenticationFilter _jwtAuthenticationFilter) {
        this._userDetailsService = _userDetailsService;
        this._passwordEncoder = _passwordEncoder;
        this._jwtAuthenticationFilter = _jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(_userDetailsService).passwordEncoder(_passwordEncoder);
        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .authorizeRequests()
                .antMatchers(Constants.WHITELISTED_URLS).permitAll()
                .antMatchers(Constants.ADMIN_URLS).hasRole(Constants.ROLE_ADMIN)
                .anyRequest()
                .authenticated();
        http.exceptionHandling()
                .authenticationEntryPoint(((request, response, authException) -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("error", "You have to login.");
                    jsonObject.put("status", HttpStatus.UNAUTHORIZED.value());
                    jsonObject.put("timestamp", new Date());
                    jsonObject.put("success", false);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(jsonObject.toString());
                }))
                .accessDeniedHandler(((request, response, accessDeniedException) -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("error", "You don't have access.");
                    jsonObject.put("status", HttpStatus.FORBIDDEN.value());
                    jsonObject.put("timestamp", new Date());
                    jsonObject.put("success", false);
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write(jsonObject.toString());
                }));
        http.authenticationManager(authenticationManager);
        http.addFilterBefore(_jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> {
            web.ignoring().antMatchers(Constants.WHITELISTED_URLS);
        });
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
