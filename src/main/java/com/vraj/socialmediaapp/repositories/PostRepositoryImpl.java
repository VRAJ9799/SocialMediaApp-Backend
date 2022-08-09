package com.vraj.socialmediaapp.repositories;

import com.vraj.socialmediaapp.dtos.PostDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Post;
import com.vraj.socialmediaapp.models.entities.User;
import com.vraj.socialmediaapp.models.entities.enums.ActivityType;
import com.vraj.socialmediaapp.repositories.interfaces.PostRepository;
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
import java.util.stream.Collectors;


@Slf4j
@Repository
public class PostRepositoryImpl implements PostRepository {

    private final UserRepository _userRepository;
    private final MongoTemplate _mongoTemplate;
    private final ModelMapper _modelMapper;

    public PostRepositoryImpl(UserRepository userRepository, MongoTemplate mongoTemplate, ModelMapper modelMapper) {
        _userRepository = userRepository;
        _mongoTemplate = mongoTemplate;
        _modelMapper = modelMapper;
    }

    @Override
    public Page<PostDto> getAllPost(Paging<PostDto> postPaging) {
        log.info("Getting all posts with {}", postPaging.toString());
        Query query = new Query();
        return getPostDtos(postPaging, query);
    }

    @Override
    public Page<PostDto> getMyFeed(Paging<PostDto> postPaging, Set<Long> userIds) {
        log.info("Getting my feed.");
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").in(userIds));
        return getPostDtos(postPaging, query);
    }


    @Override
    public Page<PostDto> getMyPosts(Paging<PostDto> postPaging, Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(userId));
        return getPostDtos(postPaging, query);
    }

    @Override
    public PostDto getPostById(String postId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(postId));
        Post post = _mongoTemplate.findOne(query, Post.class);
        if (post == null) throw new StatusException("Post doesn't exist.", HttpStatus.NOT_FOUND);
        PostDto postDto = _modelMapper.map(post, PostDto.class);
        return postDto;
    }

    @Override
    public List<UserDto> getPostLikes(String postId) {
        Post post = _mongoTemplate.findById(postId, Post.class);
        if (post == null) throw new StatusException("Post doesn't exist.", HttpStatus.NOT_FOUND);
        List<User> users = _userRepository.findByIdIn(post.getLikedBy());
        List<UserDto> userDtos = users.stream().filter(u -> !u.isDeleted()).map(u -> _modelMapper.map(u, UserDto.class)).collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public PostDto createPost(Post post) {
        post = _mongoTemplate.save(post);
        PostDto postDto = _modelMapper.map(post, PostDto.class);
        return postDto;
    }

    @Override
    public void postAction(ActivityType activityType, String postId, Long userId) {
        Query query = Query.query(Criteria.where("id").is(postId));
        query.addCriteria(Criteria.where("deleted").is(false));
        Update update = new Update();
        switch (activityType) {
            case LIKE -> {
                update.addToSet("likedBy", userId);
            }
            case UNLIKE -> {
                update.pull("likedBy", userId);
            }
            case SAVE -> {
                update.addToSet("savedBy", userId);
            }
            case UNSAVE -> {
                update.pull("savedBy", userId);
            }
        }
        _mongoTemplate.findAndModify(query, update, Post.class);
    }

    @Override
    public void deletePostById(String postId) {
        Query query = Query.query(Criteria.where("id").is(postId));
        query.addCriteria(Criteria.where("deleted").is(false));
        Update update = Update.update("deleted", true);
        _mongoTemplate.findAndModify(query, update, Post.class);
    }


    private Page<PostDto> getPostDtos(Paging<PostDto> postPaging, Query query) {
        PageRequest pageRequest = PageRequest.of(postPaging.getPageNo(), postPaging.getLimit(), Sort.by(postPaging.getSortOrder(), postPaging.getSortBy()));
        query.with(pageRequest);
        postPaging.getFilters().forEach((key, value) -> {
            if (value instanceof String) {
                query.addCriteria(Criteria.where(key).regex("*" + value + "*", "i"));
            } else if (value instanceof Boolean || value instanceof Number) {
                query.addCriteria(Criteria.where(key).is(value));
            }
        });
        List<Post> postList = _mongoTemplate.find(query, Post.class);
        List<PostDto> postDtos = getPostsWithUsers(postList);
        Page<PostDto> posts = PageableExecutionUtils.getPage(
                postDtos,
                pageRequest,
                () -> _mongoTemplate.count(query, Post.class)
        );
        return posts;
    }

    private List<PostDto> getPostsWithUsers(List<Post> posts) {
        Set<Long> userIds = new HashSet<>();
        List<PostDto> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            userIds.add(post.getUserId());
            userIds.addAll(post.getLikedBy());
        });
        // call user repository to get users
        List<User> users = _userRepository.findByIdIn(userIds);
        posts.forEach(post -> {
            PostDto postDto = _modelMapper.map(post, PostDto.class);
            Optional<User> createdBy = users.stream().filter(u -> u.getId().equals(post.getUserId())).findFirst();
            if (createdBy.isPresent()) {
                UserDto createdByUser = _modelMapper.map(createdBy.get(), UserDto.class);
                postDto.setUser(createdByUser);
                postDto.setLikedBy(
                        users.stream().filter(u -> post.getLikedBy().contains(u.getId()) && !u.isDeleted()).map(u -> u.getId()).collect(Collectors.toSet())
                );
                postDtos.add(postDto);
            }
        });

        return postDtos;
    }
}
