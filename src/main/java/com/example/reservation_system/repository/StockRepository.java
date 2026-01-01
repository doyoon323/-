package com.example.reservation_system.repository;

import com.example.reservation_system.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}