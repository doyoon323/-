package com.example.reservation_system.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.reservation_system.dto.request.UserSignUpRequest;
import com.example.reservation_system.dto.response.UserResponse;
import com.example.reservation_system.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor //생성자를 알아서 만들어줌 . this.__ = 이런거 
public class UserController {
    
    private final UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<UserResponse> signUp(@Validated @RequestBody UserSignUpRequest signUpDto) {// @RequestBody: 프론트의 json데이터를 dto로 변환 
        UserResponse response = userService.createUser(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/login")
    public String login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        Long userId = userService.login(loginData.get("loginId"), loginData.get("password"));
    
        //세션에 유저 ID 저장 -> 주문 기능에서 꺼내쓸 수 있음
        HttpSession session = request.getSession();
        session.setAttribute("USER_ID", userId);
        return "로그인 성공";
    }

    @GetMapping("/api/me")
    public String getMe(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        
        if (userId == null) {
            return "로그인이 필요합니다!";
        }
        return "현재 로그인한 유저 ID는: " + userId;
    }
}
