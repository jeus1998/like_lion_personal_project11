package com.example.miniproject_basic_baejeu.controller;

import com.example.miniproject_basic_baejeu.dto.ResponseDto;
import com.example.miniproject_basic_baejeu.entity.UserEntity;
import com.example.miniproject_basic_baejeu.repository.UserRepository;
import com.example.miniproject_basic_baejeu.security.CustomUserDetails;
import com.example.miniproject_basic_baejeu.security.JwtTokenDto;
import com.example.miniproject_basic_baejeu.security.JwtTokenUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


// POSTMAN이 아닌 브라우저로 회원가입, 로그인, 로그인 성공시 -> 현재 사용자 정보
@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    public UserController(
            UserRepository userRepository,
            UserDetailsManager manager,
            PasswordEncoder passwordEncoder,
            JwtTokenUtils jwtTokenUtils
    )
    {
        this.userRepository = userRepository;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }
    // /users/register 회원가입
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("passwordCheck") String passwordCheck,
                               @RequestParam("address") String address,
                               @RequestParam("phone") String phone,
                               @RequestParam("email") String email,
                               Model model) {
        if (password.equals(passwordCheck)) {
            // 회원 가입 로직 수행
            manager.createUser(CustomUserDetails.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .phone(phone)
                    .address(address)
                    .email(email)
                    .build());

            // 가입 성공 시 로그인 폼으로 리다이렉트
            return "redirect:/users/issue";
        } else {
            // 가입 실패 시 다시 회원 가입 폼으로 이동하며 에러 메시지 전달
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "register";
        }
    }
    @GetMapping("/main")
    public String showMainForm() {
        return "main";
    }
    // /users/issue 로그인
    @GetMapping("/issue")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity createJwt(@NotBlank @RequestParam String username, @NotBlank @RequestParam String password) {
        UserDetails userDetails = manager.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        String token = jwtTokenUtils.generateToken(userDetails);
        // 토큰 발행까지는 성공
        log.info(token);

        Map<String, String> Token = new HashMap<>();
        Token.put("token", token);

        // JWT 토큰을 클라이언트에게 전달
        return ResponseEntity.ok(Token);
    }

    @GetMapping("/profile")
    public String seeProfile(){
        log.info("여기까진 통과");
        return "profile";
    }

    // 여기서 프로필 업데이트 코드 작성 해놓기
    @PostMapping("/profile")
    @ResponseBody
    public ResponseEntity<?> showUserProfile(Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        log.info("프로필 페이지 접근: {}", username);

        ResponseDto response = new ResponseDto();
        response.setMessage("안녕하세요");
        return ResponseEntity.ok(response);
    }


}
