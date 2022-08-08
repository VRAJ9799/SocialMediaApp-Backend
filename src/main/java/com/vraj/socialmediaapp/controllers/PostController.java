package com.vraj.socialmediaapp.controllers;

import com.vraj.socialmediaapp.dtos.CreatePostDto;
import com.vraj.socialmediaapp.dtos.PostDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.helpers.ServiceHelper;
import com.vraj.socialmediaapp.models.commons.ApiResponse;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.services.interfaces.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin("*")
public class PostController {

    private final PostService _postService;

    public PostController(PostService postService) {
        _postService = postService;
    }

    @GetMapping("/my-feed")
    public ResponseEntity<?> GetMyFeed(Paging postPaging) {
        if (postPaging == null) postPaging = new Paging<>();
        Page<PostDto> posts = _postService.getMyFeed(postPaging);
        return ServiceHelper.getPagedResult(postPaging, posts);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<?> GetMyPosts(Paging<PostDto> postPaging) {
        if (postPaging == null) postPaging = new Paging<>();
        Page<PostDto> posts = _postService.getMyPosts(postPaging);
        return ServiceHelper.getPagedResult(postPaging, posts);
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<?> getPostById(@PathVariable(name = "post_id") String postId) {
        PostDto post = _postService.getPostById(postId);
        ApiResponse<PostDto> apiResponse = new ApiResponse(post);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/likes/{post_id}")
    public ResponseEntity<?> getPostLikes(@PathVariable(name = "post_id") String postId) {
        List<UserDto> postLikes = _postService.getPostLikes(postId);
        ApiResponse apiResponse = new ApiResponse(postLikes);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@Valid @RequestBody @ModelAttribute CreatePostDto createPostDto) {
        PostDto post = _postService.createPost(createPostDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{post_id}")
                .buildAndExpand(post.getId())
                .toUri();
        ApiResponse<URI> apiResponse = new ApiResponse<>(uri);
        apiResponse.setStatus(HttpStatus.CREATED.value());
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PutMapping("/like/{post_id}")
    public ResponseEntity<?> likePost(@PathVariable(name = "post_id") String postId) {
        _postService.likePost(postId);
        ApiResponse<String> apiResponse = new ApiResponse<>("you liked this post.");
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/un-like/{post_id}")
    public ResponseEntity<?> unlikePost(@PathVariable(name = "post_id") String postId) {
        _postService.unlikePost(postId);
        ApiResponse<String> apiResponse = new ApiResponse<>("you un-liked this post.");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePostById(@PathVariable(name = "post_id") String postId) {
        _postService.deletePostById(postId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Post deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

}
