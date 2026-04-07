package com.testing.load.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table("users")
public class User {

    @Id
    private Long id;
    private String username;
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public User(String username) {
        this.username = username;
    }
}
