package com.vraj.socialmediaapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateRole {

    private Long id;

    @Size(min = 5, max = 50, message = "'${validatedValue}' must be between {min} and {max}.")
    @Pattern(regexp = "^[a-z]+$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "'${validatedValue}' contain only Alphabets.")
    private String name;

    public AddOrUpdateRole(String name) {
        this.name = name;
    }
}
