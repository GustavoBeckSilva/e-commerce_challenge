package com.compass.e_commerce_challenge.service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.dto.user.UpdateUserRequest;
import com.compass.e_commerce_challenge.dto.user.UserResponse;
import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.util.exceptions.OperationNotAllowedException;
import com.compass.e_commerce_challenge.util.exceptions.ResourceNotFoundException;
import com.compass.e_commerce_challenge.util.exceptions.UserAlreadyExistsException;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private User getAuthenticatedUserEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email) //
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUserEntity();
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest dto) {
        User user = getAuthenticatedUserEntity();
        
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(dto.getEmail())) { //
            throw new UserAlreadyExistsException("The email '" + dto.getEmail() + "' is already in use by another user.");
        }

        if (dto.getUsername() != null && !dto.getUsername().equalsIgnoreCase(user.getUsername()) && userRepository.existsByUsername(dto.getUsername())) { //
            throw new UserAlreadyExistsException("The username '" + dto.getUsername() + "' is already in use by another user.");
        }

        modelMapper.map(dto, user); 
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> listUsers(PageRequestDto pageRequest) {
        PageRequest pr = PageRequest.of(
                pageRequest.getPage(),
                pageRequest.getSize(),
                Sort.by(Sort.Direction.fromString(pageRequest.getDirection()), pageRequest.getSortBy())
        );
        Page<User> page = userRepository.findAll(pr); //
        return PagedResponse.<UserResponse>builder()
                .content(page.map(u -> modelMapper.map(u, UserResponse.class)).getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ApiResponse<?> deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
   
        User currentUser = getAuthenticatedUserEntity();
        if (currentUser.getId().equals(userToDelete.getId())) {
            throw new OperationNotAllowedException("Administrators cannot delete their own accounts.");
        }

        userRepository.delete(userToDelete);
        return ApiResponse.success("User deleted successfully.");
    }
}