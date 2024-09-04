package com.commerce.application.ports.driven;

import com.commerce.domain.api.PriceApi;

import java.time.LocalDateTime;

/**
 * The interface Price repository port.
 */
public interface PriceRepositoryPort {

    /**
     * Gets price.
     *
     * @param productId       the product id
     * @param brandId         the brand id
     * @param applicationDate the application date
     * @return the price
     */
    PriceApi getPrice(Long productId, Long brandId, LocalDateTime applicationDate);
}
