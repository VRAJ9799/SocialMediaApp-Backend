package com.vraj.socialmediaapp.models.commons;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "email-templates")
@Configuration("emailTemplates")
@Data
public class EmailTemplates {
    private String emailVerification;
}
