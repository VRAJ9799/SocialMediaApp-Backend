package com.vraj.socialmediaapp.services.interfaces;

import com.vraj.socialmediaapp.dtos.CreatePostDto;
import com.vraj.socialmediaapp.dtos.PostDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.models.commons.Paging;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    Page<PostDto> getAllPost(Paging<PostDto> postPaging);

    Page<PostDto> getMyFeed(Paging<PostDto> postPaging);

    Page<PostDto> getMyPosts(Paging<PostDto> postPaging);

    List<UserDto> getPostLikes(String postId);

    PostDto getPostById(String postId);

    PostDto createPost(CreatePostDto createPostDto);

    void likePost(String postId);

    void unlikePost(String postId);

    void deletePostById(String postId);

}
