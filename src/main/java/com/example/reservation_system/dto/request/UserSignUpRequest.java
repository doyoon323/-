package com.example.reservation_system.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserSignUpRequest{
    private String email;
    private String password;
    private String username;
}