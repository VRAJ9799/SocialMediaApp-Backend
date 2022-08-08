package com.vraj.socialmediaapp.helpers;

import com.vraj.socialmediaapp.models.AppUser;
import com.vraj.socialmediaapp.models.commons.ApiResponse;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class ServiceHelper {

    public static <U> ResponseEntity<?> getPagedResult(Paging<U> paging, Page<U> data) {
        paging.setHasNext(data.hasNext());
        paging.setHasPrevious(data.hasPrevious());
        paging.setData(data.getContent());
        ApiResponse<Paging<U>> apiResponse = new ApiResponse<>(paging);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    public static User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof AppUser appUser) {
            return appUser.getUser();
        }
        return null;
    }

    public static UserDetails getLoggedInUserDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails;
    }
}
