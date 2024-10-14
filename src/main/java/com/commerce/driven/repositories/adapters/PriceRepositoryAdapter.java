package com.commerce.driven.repositories.adapters;

import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

/**
 * The type Price repository adapter.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    private final PriceMORepository priceMORepository;
    private final PriceMapper priceMapper;

    @Override
    @Retry(name = "priceRepository", fallbackMethod = "fallbackRetry")
    @Cacheable(value = "prices", key = "#productId + '_' + #brandId + '_' + #applicationDate")
    public CompletableFuture<List<Price>> getPrices(Long productId, Long brandId, LocalDateTime applicationDate) {
        log.info("Fetching prices from database for productId {}, brandId {} at {}", productId, brandId, applicationDate);

        return CompletableFuture.supplyAsync(() -> {
            var pricesMO = priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    brandId, productId, applicationDate, applicationDate);

            return pricesMO.stream().map(priceMapper::mapPriceMOToPrice).collect(toList());
        });
    }

    /**
     * Fallback retry completable future.
     *
     * @param productId       the product id
     * @param brandId         the brand id
     * @param applicationDate the application date
     * @param throwable       the throwable
     * @return the completable future
     */
    public CompletableFuture<List<Price>> fallbackRetry(Long productId, Long brandId, LocalDateTime applicationDate, Throwable throwable) {
        log.error("Retry failed after multiple attempts.: {}, {}, {}. Reason: {}", productId, brandId, applicationDate, throwable.getMessage());
        throw new RuntimeException(
                "Failed after retries");
    }
}
