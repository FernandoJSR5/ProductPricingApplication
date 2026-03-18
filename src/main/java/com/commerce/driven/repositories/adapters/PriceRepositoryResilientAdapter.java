package com.commerce.driven.repositories.adapters;

import com.commerce.common.exceptions.FallbackPriceException;
import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceRepositoryResilientAdapter {

    private final PriceMORepository priceMORepository;
    private final PriceMapper priceMapper;

    @Retry(name = "priceRepository", fallbackMethod = "fallbackRetry")
    @Cacheable(value = "prices", key = "#productId + '_' + #brandId + '_' + #applicationDate")
    public List<Price> getPrices(Long productId, Long brandId, LocalDateTime applicationDate) {
        log.info("Fetching prices from database for productId {}, brandId {} at {}", productId, brandId, applicationDate);

        var pricesMO = priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                brandId, productId, applicationDate, applicationDate);

        return pricesMO.stream()
                .map(priceMapper::mapPriceMOToPrice)
                .toList();
    }

    public List<Price> fallbackRetry(Long productId, Long brandId, LocalDateTime applicationDate, Throwable throwable) {
        log.error("Fallback triggered for productId {}, brandId {} at {}", productId, brandId, applicationDate, throwable);

        throw new FallbackPriceException();
    }
}