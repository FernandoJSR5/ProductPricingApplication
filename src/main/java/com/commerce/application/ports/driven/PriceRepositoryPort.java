package com.commerce.application.ports.driven;

import com.commerce.domain.entities.Price;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Price repository port.
 */
public interface PriceRepositoryPort {

    /**
     * Gets prices.
     *
     * @param productId       the product id
     * @param brandId         the brand id
     * @param applicationDate the application date
     * @return the prices
     */
    List<Price> getPrices(Long productId, Long brandId, LocalDateTime applicationDate);
}
