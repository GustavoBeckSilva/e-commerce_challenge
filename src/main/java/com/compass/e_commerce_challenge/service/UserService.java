package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.dto.user.UpdateUserRequest;
import com.compass.e_commerce_challenge.dto.user.UserResponse;

public interface UserService {

	/*
	 * Shared
	 */
	
	UserResponse getCurrentUser();
    UserResponse updateCurrentUser(UpdateUserRequest dto);
    ApiResponse<?> deleteUser(Long userId);

    /*
	 * Admin
	 */
    
    PagedResponse<UserResponse> listUsers(PageRequestDto pageRequest);
}
