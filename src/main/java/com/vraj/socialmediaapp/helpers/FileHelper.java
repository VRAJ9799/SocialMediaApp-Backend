package com.vraj.socialmediaapp.helpers;

import com.cloudinary.Cloudinary;
import com.vraj.socialmediaapp.exceptions.StatusException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FileHelper {
    private final Cloudinary _cloudinary;

    public FileHelper(Cloudinary cloudinary) {
        _cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile multipartFile) {
        Long folder_name = 1L;//ServiceHelper.getLoggedInUser().getId();
        String secure_url;
        File file = getFileFromMultipartFile(multipartFile);
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("folder", folder_name.toString());
            config.put("resource_type", "auto");
            config.put("use_filename", true);
            config.put("type", "upload");
            config.put("allowed_formats", "jpg,jpeg,png,mp4,gif");
            config.put("return_delete_token", true);
            Map response = _cloudinary.uploader().upload(file, config);
            secure_url = response.get("secure_url").toString();
        } catch (IOException ioException) {
            throw new StatusException("Error while uploading file.", HttpStatus.BAD_REQUEST);
        } finally {
            file.delete();
        }
        return secure_url;
    }

    private File getFileFromMultipartFile(MultipartFile multipartFile) {
        String file_name = FilenameUtils.getBaseName(multipartFile.getOriginalFilename()) + "-" + UUID.randomUUID() + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        File file = new File(file_name);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
        } catch (IOException ioException) {
            throw new StatusException("Error while uploading file.", HttpStatus.BAD_REQUEST);
        }
        return file;
    }
}
