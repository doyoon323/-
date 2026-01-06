package com.example.reservation_system.service;

import com.example.reservation_system.entity.Stock;
import com.example.reservation_system.entity.User;
import com.example.reservation_system.repository.ProductRepository;
import com.example.reservation_system.repository.ReservationRepository;
import com.example.reservation_system.repository.StockRepository;
import com.example.reservation_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MultiThreadRequest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private Long productId;
    private Long userId;

   @BeforeEach
    void setUp() {
        // 1. 기존 데이터 삭제 (삭제가 안 될 경우를 대비해 순서대로 진행)
        reservationRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // 2. [핵심] 실행할 때마다 매번 달라지는 고유 번호 생성
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());

        // 3. 유니크 제약조건이 걸린 모든 필드에 uniqueSuffix를 붙입니다.
        User user = User.builder()
                .loginId("user" + uniqueSuffix)            // ⬅️ 로그에 나온 그 에러 해결 지점!
                .password("password123!")
                .nickname("테스터" + uniqueSuffix)
                .email("test" + uniqueSuffix + "@example.com") // ⬅️ 이메일 중복도 방지
                .phoneNumber("010-0000-0000")
                .build();
        
        userId = userRepository.save(user).getId();

        // 4. 상품명도 안전하게 중복 방지
        productId = productService.createProduct("멀티상품" + uniqueSuffix, 2000, 100);
    }

    @Test
    @DisplayName("동시성 테스트: 100개의 재고에 대해 100명이 동시에 1개씩 예약 시도")
    void multiThreadReservation() throws InterruptedException {
        int threadCount = 100;
        // 동시에 일할 일꾼 32명 소환
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 작업이 다 끝날 때까지 기다리기 위한 장치
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 100명이 동시에 주문 시작
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                executorService.submit(() -> {
                try {
                    reservationService.createReservation(userId, productId, 1);
                } catch (Exception e) {
                    // e.getMessage() 대신 e.printStackTrace()를 써서 범인을 확실히 잡습니다.
                    e.printStackTrace(); 
                } finally {
                    latch.countDown();
                }
            });
            });
        }

        latch.await(); // 모든 일꾼이 일을 끝낼 때까지 메인 쓰레드 대기

        // then: 최종 재고 확인
        Stock stock = stockRepository.findByProductId(productId).orElseThrow();
        
        System.out.println("======================================");
        System.out.println("최종 남은 재고: " + stock.getQuantity());
        System.out.println("======================================");
        
        // 100개 중 100명이 가져갔으므로 정확히 0이어야 함
        assertEquals(0, stock.getQuantity(), "동시성 제어가 안 되면 재고가 0보다 크게 남습니다.");
    }
}