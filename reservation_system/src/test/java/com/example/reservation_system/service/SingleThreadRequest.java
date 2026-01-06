package com.example.reservation_system.service;

import com.example.reservation_system.entity.Stock;
import com.example.reservation_system.entity.User;
import com.example.reservation_system.repository.StockRepository;
import com.example.reservation_system.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class SingleThreadRequest {

    @Autowired
    private UserRepository userRepository; // 추가

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StockRepository stockRepository;

    private Long productId;

    private Long userId; // 추가
    
    @BeforeEach
    void setUp() {
        User user = User.builder()
            .loginId("concurTest1")    // 에러 원인 해결!
            .password("password123!")
            .email("con1@example.com")
            .phoneNumber("010-1234-5678")
            .build();


        userId = userRepository.save(user).getId();
        // 테스트용 상품 등록: 이름 "싱글상품", 가격 1000, 초기재고 10개
        productId = productService.createProduct("싱글상품2", 1000, 10);
    }

    @Test
    @DisplayName("단일 요청 테스트: 10개 중 3개를 예약하면 재고가 7개 남아야 한다")
    void singleReservation() {
        reservationService.createReservation(userId, productId, 3);

        Stock stock = stockRepository.findByProductIdWithLock(productId).orElseThrow();
        assertEquals(7, stock.getQuantity());
    }
}