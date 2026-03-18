package com.commerce.driven.repositories.adapters;

import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.domain.entities.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    private final PriceRepositoryResilientAdapter retryableExecutor;

    @Override
    public CompletableFuture<List<Price>> getPrices(Long productId, Long brandId, LocalDateTime applicationDate) {
        return CompletableFuture.completedFuture(
                retryableExecutor.getPrices(productId, brandId, applicationDate)
        );
    }
}