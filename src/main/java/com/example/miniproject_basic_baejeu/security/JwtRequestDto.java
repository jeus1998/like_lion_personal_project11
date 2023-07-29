package com.example.miniproject_basic_baejeu.security;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String username;
    private String password;
}
