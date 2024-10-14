package com.commerce.application.ports.driving;

import com.commerce.domain.entities.Price;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * The interface Price service port.
 */
public interface PriceServicePort {

    /**
     * Gets applicable price.
     *
     * @param productId       the product id
     * @param brandId         the brand id
     * @param applicationDate the application date
     * @return the applicable price
     */
    CompletableFuture<Price> getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate);

}
