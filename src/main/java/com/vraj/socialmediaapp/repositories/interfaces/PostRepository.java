package com.vraj.socialmediaapp.repositories.interfaces;

import com.vraj.socialmediaapp.dtos.PostDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Post;
import com.vraj.socialmediaapp.models.entities.enums.ActivityType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface PostRepository {

    Page<PostDto> getAllPost(Paging<PostDto> postPaging);

    Page<PostDto> getMyFeed(Paging<PostDto> postPaging, Set<Long> userIds);

    Page<PostDto> getMyPosts(Paging<PostDto> postPaging, Long userId);

    PostDto getPostById(String postId);

    List<UserDto> getPostLikes(String postId);

    PostDto createPost(Post post);

    void postAction(ActivityType activityType, String postId, Long userId);

    void deletePostById(String postId);

}
