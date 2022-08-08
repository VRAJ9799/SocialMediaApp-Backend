package com.vraj.socialmediaapp.services;

import com.vraj.socialmediaapp.dtos.CreatePostDto;
import com.vraj.socialmediaapp.dtos.PostDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.helpers.FileHelper;
import com.vraj.socialmediaapp.helpers.ServiceHelper;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Post;
import com.vraj.socialmediaapp.models.entities.User;
import com.vraj.socialmediaapp.models.entities.enums.ActivityType;
import com.vraj.socialmediaapp.repositories.interfaces.FollowerRepository;
import com.vraj.socialmediaapp.repositories.interfaces.PostRepository;
import com.vraj.socialmediaapp.services.interfaces.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository _postRepository;

    private final FollowerRepository _followerRepository;
    private final FileHelper _fileHelper;

    public PostServiceImpl(PostRepository postRepository, FollowerRepository followerRepository, FileHelper fileHelper) {
        _postRepository = postRepository;
        _followerRepository = followerRepository;
        _fileHelper = fileHelper;
    }

    @Override
    public Page<PostDto> getAllPost(Paging<PostDto> postPaging) {
        Page<PostDto> posts = _postRepository.getAllPost(postPaging);
        return posts;
    }

    @Override
    public Page<PostDto> getMyFeed(Paging<PostDto> postPaging) {
        Long curr_user = ServiceHelper.getLoggedInUser().getId();
        Set<Long> followers = new HashSet<>();
        followers.add(curr_user);
        followers.addAll(_followerRepository.getFollowers(curr_user).stream().filter(u -> !u.isDeleted()).map(User::getId).collect(Collectors.toSet()));
        Page<PostDto> myFeed = _postRepository.getMyFeed(postPaging, followers);
        return myFeed;
    }

    @Override
    public Page<PostDto> getMyPosts(Paging<PostDto> postPaging) {
        Long curr_user = ServiceHelper.getLoggedInUser().getId();
        Page<PostDto> myPosts = _postRepository.getMyPosts(postPaging, curr_user);
        return myPosts;
    }

    @Override
    public List<UserDto> getPostLikes(String postId) {
        List<UserDto> postLikes = _postRepository.getPostLikes(postId);
        return postLikes;
    }

    @Override
    public PostDto getPostById(String postId) {
        PostDto postDto = _postRepository.getPostById(postId);
        return postDto;
    }

    @Override
    public PostDto createPost(CreatePostDto createPostDto) {
        Long curr_user = ServiceHelper.getLoggedInUser().getId();
        Post post = new Post();
        post.setCaption(createPostDto.getCaption());
        for (MultipartFile multipartFile : createPostDto.getFiles()) {
            String image_url = _fileHelper.uploadFile(multipartFile);
            post.addImage(image_url);
        }
        post.setUserId(curr_user);
        PostDto postDto = _postRepository.createPost(post);
        return postDto;
    }

    @Override
    public void likePost(String postId) {
        Long curr_user = ServiceHelper.getLoggedInUser().getId();
        _postRepository.postAction(ActivityType.UNLIKE, postId, curr_user);
    }

    @Override
    public void unlikePost(String postId) {
        Long curr_user = ServiceHelper.getLoggedInUser().getId();
        _postRepository.postAction(ActivityType.LIKE, postId, curr_user);
    }

    @Override
    public void deletePostById(String postId) {
        _postRepository.deletePostById(postId);
    }
}
