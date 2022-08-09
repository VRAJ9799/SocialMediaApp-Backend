package com.vraj.socialmediaapp.controllers;

import com.vraj.socialmediaapp.dtos.CommentDto;
import com.vraj.socialmediaapp.dtos.CreateCommentDto;
import com.vraj.socialmediaapp.helpers.ServiceHelper;
import com.vraj.socialmediaapp.models.commons.ApiResponse;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.services.interfaces.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/posts/{post_id}/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService _commentService;

    public CommentController(CommentService commentService) {
        _commentService = commentService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getPostComments(Paging<CommentDto> commentPaging, @PathVariable(name = "post_id") String postId) {
        if (commentPaging == null) commentPaging = new Paging<>();
        Page<CommentDto> comments = _commentService.getCommentsByPost(commentPaging, postId, false);
        return ServiceHelper.getPagedResult(commentPaging, comments);
    }

    @PostMapping("/")
    public ResponseEntity<?> createComment(@Valid @RequestBody CreateCommentDto createCommentDto, @PathVariable(name = "post_id") String postId) {
        CommentDto comment = _commentService.createComment(postId, createCommentDto);
        ApiResponse<CommentDto> apiResponse = new ApiResponse<>(comment);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable(name = "comment_id") String commentId) {
        _commentService.deleteComment(commentId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Comment deleted succesfully.");
        return ResponseEntity.ok(apiResponse);
    }
}
