package com.vraj.socialmediaapp.services;

import com.vraj.socialmediaapp.dtos.CommentDto;
import com.vraj.socialmediaapp.dtos.CreateCommentDto;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.repositories.interfaces.CommentRepository;
import com.vraj.socialmediaapp.services.interfaces.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository _commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        _commentRepository = commentRepository;
    }

    @Override
    public Page<CommentDto> getCommentsByPost(Paging<CommentDto> commentPaging, String postId, boolean isDeleted) {
        Page<CommentDto> comments = _commentRepository.getCommentsByPost(commentPaging, postId, isDeleted);
        return comments;
    }

    @Override
    public CommentDto createComment(String postId, CreateCommentDto createCommentDto) {
        CommentDto comment = _commentRepository.createComment(postId, createCommentDto);

        return comment;
    }

    @Override
    public void deleteComment(String commentId) {
        _commentRepository.deleteComment(commentId);
    }
}
