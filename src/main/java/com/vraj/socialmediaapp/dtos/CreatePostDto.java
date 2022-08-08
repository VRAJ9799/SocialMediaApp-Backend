package com.vraj.socialmediaapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {

    @NotBlank(message = "Caption is required.")
    @Size(max = 500, message = "Caption should be more than {max} characters.")
    private String caption;

    @NotEmpty(message = "At-least one image is required.")
    private Set<MultipartFile> files = new HashSet<>();

}
