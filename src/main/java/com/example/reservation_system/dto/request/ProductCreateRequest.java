package com.example.reservation_system.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductCreateRequest {
    private String name;
    private Integer price;
    private String type;
}
