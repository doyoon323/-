package com.example.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // DB에 외래키(FK) 컬럼이 생깁니다
    private Product product;

    @Builder
    public Stock(Integer quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }
}