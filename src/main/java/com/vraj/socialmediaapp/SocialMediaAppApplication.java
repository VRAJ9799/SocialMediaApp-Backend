package com.vraj.socialmediaapp;

import com.cloudinary.Cloudinary;
import com.sendgrid.SendGrid;
import com.vraj.socialmediaapp.helpers.Constants;
import com.vraj.socialmediaapp.repositories.interfaces.RoleRepository;
import com.vraj.socialmediaapp.repositories.interfaces.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = Constants.TITLE,
                version = Constants.VERSION,
                contact = @Contact(
                        email = Constants.EMAIL,
                        name = Constants.NAME
                )
        )
)
@SecurityScheme(
        name = Constants.SECURITY_NAME,
        scheme = Constants.SECURITY_SCHEME,
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class SocialMediaAppApplication {

    private final UserRepository _userRepo;
    private final RoleRepository _roleRepo;

    public SocialMediaAppApplication(UserRepository userRepo, RoleRepository roleRepo) {
        _userRepo = userRepo;
        _roleRepo = roleRepo;
    }

    public static void main(String[] args) {
        log.info("Application is running...");
        SpringApplication.run(SocialMediaAppApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Cloudinary cloudinary(
            @Value("${cloudinary.api_secret}") String secretKey,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.cloud_name}") String cloudName
    ) {
        Cloudinary cloudinary = new Cloudinary();
        cloudinary.config.secure = true;
        cloudinary.config.apiSecret = secretKey;
        cloudinary.config.apiKey = apiKey;
        cloudinary.config.cloudName = cloudName;
        return cloudinary;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SendGrid sendGrid(
            @Value("${sendgrid.api-key}") String apiKey
    ) {
        return new SendGrid(apiKey);
    }

}
