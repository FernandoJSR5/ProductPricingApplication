package com.commerce.application.ports.driving;

import com.commerce.domain.entities.Price;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface PriceServicePort {
    CompletableFuture<Price> getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate);
}
