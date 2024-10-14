package com.commerce.application.services;

import com.commerce.common.exceptions.FallbackPriceException;
import com.commerce.common.exceptions.PriceException;
import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.entities.Price;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

/**
 * The type Price service use case.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PriceServiceUseCase implements PriceServicePort {

    private final PriceRepositoryPort priceRepositoryPort;

    @Override
    @CircuitBreaker(name = "priceService", fallbackMethod = "fallbackPrices")
    public CompletableFuture<Price> getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        return priceRepositoryPort.getPrices(productId, brandId, applicationDate)
                .thenApply(prices ->
                        prices.stream().max(Comparator.comparing(Price::getPriority))
                                .orElseThrow(PriceException::new));
    }

    /**
     * Fallback prices completable future.
     *
     * @param productId       the product id
     * @param brandId         the brand id
     * @param applicationDate the application date
     * @param throwable       the throwable
     * @return the completable future
     */
    public CompletableFuture<Price> fallbackPrices(Long productId, Long brandId, LocalDateTime applicationDate, Throwable throwable) {
        String throwableClassName = throwable.getClass().getSimpleName();

        switch (throwableClassName) {
            case "PriceException":
                log.warn("Handled PriceException for productId: {}, brandId: {}, applicationDate: {}", productId, brandId, applicationDate);
                return CompletableFuture.failedFuture(new PriceException());
            case "RuntimeException":
                log.error("RuntimeException occurred for productId: {}, brandId: {}, applicationDate: {}. Reason: {}",
                        productId, brandId, applicationDate, throwable.getMessage());
                return CompletableFuture.failedFuture(new FallbackPriceException());
            default:
                log.error("Fallback method called for getPrices: {}, {}, {}. Reason: {}",
                        productId, brandId, applicationDate, throwable.getMessage());
                return CompletableFuture.failedFuture(new FallbackPriceException());
        }
    }

}
