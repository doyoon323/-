package com.example.reservation_system.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.reservation_system.dto.request.UserSignUpRequest;
import com.example.reservation_system.dto.response.UserResponse;
import com.example.reservation_system.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

}
