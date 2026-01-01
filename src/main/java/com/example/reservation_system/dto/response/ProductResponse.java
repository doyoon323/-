package com.example.reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse{
    private Long id;
    private String name;
    private Integer price;
}