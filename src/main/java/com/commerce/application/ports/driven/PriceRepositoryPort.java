package com.commerce.application.ports.driven;

import com.commerce.domain.entities.Price;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PriceRepositoryPort {
    CompletableFuture<List<Price>> getPrices(Long productId, Long brandId, LocalDateTime applicationDate);
}
