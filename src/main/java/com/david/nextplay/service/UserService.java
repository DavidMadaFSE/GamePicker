package com.david.nextplay.service;

import org.springframework.stereotype.Service;

import com.david.nextplay.dto.user.UserResponse;
import com.david.nextplay.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    public UserResponse getCurrentUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getDisplayUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
