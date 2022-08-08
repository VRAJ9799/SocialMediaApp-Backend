package com.vraj.socialmediaapp.services.interfaces;


import com.vraj.socialmediaapp.dtos.CommentDto;
import com.vraj.socialmediaapp.dtos.CreateCommentDto;
import com.vraj.socialmediaapp.models.commons.Paging;
import org.springframework.data.domain.Page;

public interface CommentService {

    Page<CommentDto> getCommentsByPost(Paging<CommentDto> commentPaging, String postId, boolean isDeleted);

    CommentDto createComment(String postId, CreateCommentDto createCommentDto);

    void deleteComment(String commentId);

}
