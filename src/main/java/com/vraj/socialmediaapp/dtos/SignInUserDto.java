package com.vraj.socialmediaapp.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignInUserDto {
    @NotBlank(message = "Email is required.")
    @Size(max = 255, message = " '${validatedValue}' should not contain more than {max} characters.")
    @Email(message = "'${validatedValue}' is not a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 16, message = "Password should be {min} - {max} characters.")
    private String password;
}
