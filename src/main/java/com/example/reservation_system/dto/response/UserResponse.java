package com.example.reservation_system.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor //클래스의 모든 필드를 파라미터로 받는 생성자를 자동으로 만들어준다. public ProductResponse(Long id, String name, Integer price)이거 자동으로 만들어줌 
public class UserResponse {
    private Long id;
    private String email;
    private String username;
}