package com.example.miniproject_basic_baejeu.controller;

import com.example.miniproject_basic_baejeu.security.CustomUserDetails;
import com.example.miniproject_basic_baejeu.security.JwtRegisterDto;
import com.example.miniproject_basic_baejeu.security.JwtRequestDto;
import com.example.miniproject_basic_baejeu.security.JwtTokenDto;
import com.example.miniproject_basic_baejeu.security.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

        @Slf4j
        @RestController
        @RequestMapping("token")
        public class TokenController {
            private final UserDetailsManager manager;
            private final PasswordEncoder passwordEncoder;
            private final JwtTokenUtils jwtTokenUtils;
            public TokenController(
                    UserDetailsManager manager,
                    PasswordEncoder passwordEncoder,
                    JwtTokenUtils jwtTokenUtils
            )
            {
                this.manager = manager;
                this.passwordEncoder = passwordEncoder;
                this.jwtTokenUtils = jwtTokenUtils;
            }
            @PostMapping("/issue")
            public JwtTokenDto issueJwt(@RequestBody JwtRequestDto dto) {
                UserDetails userDetails
                        = manager.loadUserByUsername(dto.getUsername());

                if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

                JwtTokenDto response = new JwtTokenDto();
                response.setToken(jwtTokenUtils.generateToken(userDetails));
                return response;
            }


            @PostMapping("/register")
            public JwtRegisterDto registerUser(@RequestBody JwtRegisterDto dto){
                if (dto.getPassword().equals(dto.getPasswordCheck())) {
                    log.info("password match!");
                    manager.createUser(CustomUserDetails.builder()
                            .username(dto.getUsername())
                            .password(passwordEncoder.encode(dto.getPassword()))
                            .phone(dto.getPhone())
                            .address(dto.getAddress())
                            .email(dto.getEmail())
                            .build());
                }
                return dto;
            }
            // 현재 로그인한 User 정보를 확인하기 위한 메서드
            @GetMapping("/check")
            public String checkUser(Authentication authentication){
                String check = authentication.getName();

                return check;
    }
}
