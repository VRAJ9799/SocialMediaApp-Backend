package com.vraj.socialmediaapp.controllers;

import com.vraj.socialmediaapp.dtos.PostDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.helpers.ServiceHelper;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.services.interfaces.PostService;
import com.vraj.socialmediaapp.services.interfaces.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    private final PostService _postService;
    private final UserService _userService;

    public AdminController(PostService postService, UserService userService) {
        _postService = postService;
        _userService = userService;
    }

    @GetMapping("/all-posts")
    public ResponseEntity<?> getAllPosts(Paging<PostDto> postDtoPaging) {
        if (postDtoPaging == null) postDtoPaging = new Paging<>();
        Page<PostDto> posts = _postService.getAllPost(postDtoPaging);
        return ServiceHelper.getPagedResult(postDtoPaging, posts);
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers(Paging<UserDto> userDtoPaging) {
        if (userDtoPaging == null) userDtoPaging = new Paging<>();
        Page<UserDto> users = _userService.getAllUsers(userDtoPaging);
        return ServiceHelper.getPagedResult(userDtoPaging, users);
    }
}
