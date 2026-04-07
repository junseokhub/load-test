package com.testing.load.user.dto;

import com.testing.load.user.domain.User;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String username,
        LocalDateTime createdAt
) {

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }
}
