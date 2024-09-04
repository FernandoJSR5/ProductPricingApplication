package com.commerce.application.ports.driving;

import com.commerce.domain.api.PriceApi;

import java.time.LocalDateTime;

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
    PriceApi getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate);

}
