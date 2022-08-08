package com.vraj.socialmediaapp.repositories;

import com.vraj.socialmediaapp.dtos.CommentDto;
import com.vraj.socialmediaapp.dtos.CreateCommentDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Comment;
import com.vraj.socialmediaapp.models.entities.Post;
import com.vraj.socialmediaapp.models.entities.User;
import com.vraj.socialmediaapp.repositories.interfaces.CommentRepository;
import com.vraj.socialmediaapp.repositories.interfaces.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final UserRepository _userRepository;
    private final MongoTemplate _mongoTemplate;
    private final ModelMapper _modelMapper;

    public CommentRepositoryImpl(UserRepository userRepository, MongoTemplate mongoTemplate, ModelMapper modelMapper) {
        _userRepository = userRepository;
        _mongoTemplate = mongoTemplate;
        _modelMapper = modelMapper;
    }

    @Override
    public Page<CommentDto> getCommentsByPost(Paging<CommentDto> commentPaging, String postId, boolean isDeleted) {
        log.info("Fetching comments for post {}", postId);
        PageRequest pageRequest = PageRequest.of(commentPaging.getPageNo(), commentPaging.getLimit(), Sort.by(commentPaging.getSortOrder(), commentPaging.getSortBy()));
        Query query = new Query();
        query.with(pageRequest);
        query.addCriteria(Criteria.where("post").is(postId));
        query.addCriteria(Criteria.where("deleted").is(isDeleted));
        query.fields().exclude("post");
        List<Comment> commentList = _mongoTemplate.find(query, Comment.class);
        Page<CommentDto> comments = PageableExecutionUtils.getPage(
                getCommentsWithUsers(commentList),
                pageRequest,
                () -> _mongoTemplate.count(query, Comment.class)
        );
        return comments;
    }

    @Override
    public CommentDto createComment(String postId, CreateCommentDto createCommentDto) {
        log.info("Creating comment for post with id {}", postId);
        Post post = _mongoTemplate.findById(postId, Post.class);
        if (post.isDeleted()) {
            throw new StatusException("Invalid post id.", HttpStatus.BAD_REQUEST);
        }
        Comment comment = new Comment(
                createCommentDto.getComment(),
                post,
                1L
        );
        comment = _mongoTemplate.save(comment);
        List<CommentDto> comments = getCommentsWithUsers(new ArrayList<>(List.of(comment)));
        return comments.get(0);
    }

    @Override
    public void deleteComment(String commentId) {
        log.info("Deleting the comment with id {}", commentId);
        Query query = Query.query(Criteria.where("id").is(commentId));
        Update update = Update.update("deleted", true);
        _mongoTemplate.findAndModify(query, update, Comment.class);
    }

    private List<CommentDto> getCommentsWithUsers(List<Comment> comments) {
        Set<Long> userIds = new HashSet<>();
        List<CommentDto> commentDtos = new ArrayList<>();
        comments.forEach(comment -> {
            userIds.add(comment.getUserId());
        });
        List<User> users = _userRepository.findByIdIn(userIds);
        comments.forEach(comment -> {
            CommentDto commentDto = _modelMapper.map(comment, CommentDto.class);
            Optional<User> user = users.stream().filter(u ->
                    u.getId().equals(comment.getUserId())
            ).findFirst();
            if (user.isPresent() && !user.get().isDeleted()) {
                UserDto userDto = _modelMapper.map(user.get(), UserDto.class);
                commentDto.setUser(userDto);
                commentDtos.add(commentDto);
            }
        });
        return commentDtos;
    }


}
