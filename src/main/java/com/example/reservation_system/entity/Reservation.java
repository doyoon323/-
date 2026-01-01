package com.example.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    private String status; // 예: "PENDING", "CONFIRMED", "CANCELLED"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 유저와 다대일 관계
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // 상품과 다대일 관계
    private Product product;

    @Builder
    public Reservation(User user, Product product, String status) {
        this.user = user;
        this.product = product;
        this.status = status;
        this.reservationTime = LocalDateTime.now();
    }
}