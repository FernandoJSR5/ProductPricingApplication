package com.commerce.application.services;

import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.common.exceptions.PriceException;
import com.commerce.domain.entities.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PriceServiceUseCase implements PriceServicePort {

    private final PriceRepositoryPort priceRepositoryPort;

    @Override
    public CompletableFuture<Price> getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        return priceRepositoryPort.getPrices(productId, brandId, applicationDate)
                .thenApply(prices -> prices.stream()
                        .max(Comparator.comparing(Price::getPriority))
                        .orElseThrow(PriceException::new));
    }
}