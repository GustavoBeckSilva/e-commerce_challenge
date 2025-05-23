package com.compass.e_commerce_challenge.service;
import java.util.Optional;

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


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private User getAuthenticatedEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    @Override
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedEntity();
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest dto) {
        User user = getAuthenticatedEntity();
        modelMapper.map(dto, user);
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<UserResponse> listUsers(PageRequestDto pageRequest) {
        PageRequest pr = PageRequest.of(
                pageRequest.getPage(),
                pageRequest.getSize(),
                Sort.by(Sort.Direction.fromString(pageRequest.getDirection()), pageRequest.getSortBy())
        );
        Page<User> page = userRepository.findAll(pr);
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
    public ApiResponse<?> deleteUser(Long userId) {
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            return ApiResponse.error("User not found");
        }
        userRepository.delete(opt.get());
        return ApiResponse.success("User deleted successfully");
    }
}
