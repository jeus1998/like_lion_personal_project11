package com.example.miniproject_basic_baejeu.controller;


import com.example.miniproject_basic_baejeu.entity.UserEntity;
import com.example.miniproject_basic_baejeu.repository.UserRepository;
import com.example.miniproject_basic_baejeu.security.CustomUserDetails;
import com.example.miniproject_basic_baejeu.security.JwtTokenDto;
import com.example.miniproject_basic_baejeu.security.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    // 1. login 페이지로 온다.
    // 2. login 페이지에 아이디 비밀번호를 입력한다.
    // 3. 성공하면 my-profile 로 이동한다.
    // my-profile : 아이디(이름) 주소, 전화번호, 이메일
    @GetMapping("/login")
    public String loginForm() {
        return "login-form";
    }

    // 로그인 성공 후 로그인 여부를 판단하기 위한 GetMapping
    // 로그인 성공 하면 토큰 발행을 해야한다.

    UserRepository repository;
    @GetMapping("/my-profile")
    public String myProfile(
            Authentication authentication
    ) {
        CustomUserDetails userDetails
                = (CustomUserDetails) authentication.getPrincipal();
        log.info(userDetails.getUsername());
        log.info(userDetails.getEmail());
//      log.info(SecurityContextHolder.getContext().getAuthentication().getName());
//      log.info(((User) authentication.getPrincipal()).getUsername());
        return "my-profile";
    }
    @PostMapping("/token")
    @ResponseBody
    public JwtTokenDto makeToken( Authentication authentication){
        String check = authentication.getName();
        UserDetails userDetailsCheck = manager.loadUserByUsername(check);
        String token = jwtTokenUtils.generateToken(userDetailsCheck);

        // repository에 token 저장
        Optional<UserEntity> optionalUser = repository.findByUsername(check);
        UserEntity entity = optionalUser.get();
        // entity.setToken(token);
        repository.save(entity);

        JwtTokenDto dto = new JwtTokenDto();
        dto.setToken(token);
        return dto;
    }
    @GetMapping("/register")
    public String registerForm() {
        return "register-form";
    }

    // 어떻게 사용자를 관리하는지는
    // interface 기반으로 의존성 주입
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    public UserController(
            UserDetailsManager manager,
            PasswordEncoder passwordEncoder,
            JwtTokenUtils jwtTokenUtils,
            UserRepository repository
    ) {
        this.repository = repository;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }
    @PostMapping("/register")
    public String registerPost(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("password-check") String passwordCheck,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email

    ) {
        if (password.equals(passwordCheck)) {
            log.info("password match!");
            // username 중복도 확인해야 하지만,
            // 이 부분은 Service 에서 진행하는 것도 나쁘지 않아보임
//            manager.createUser(User.withUsername(username)
//                    .password(passwordEncoder.encode(password))
//                    .build());
            manager.createUser(CustomUserDetails.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .phone(phone)
                    .address(address)
                    .email(email)
                    .build());
            return "redirect:/users/login";
        }
        log.warn("password does not match...");
        return "redirect:/users/register?error";
    }
}
