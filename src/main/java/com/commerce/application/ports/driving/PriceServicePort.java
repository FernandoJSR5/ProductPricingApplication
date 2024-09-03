package com.commerce.application.ports.driving;

import com.commerce.domain.api.PriceApi;

import java.time.OffsetDateTime;

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
    PriceApi getApplicablePrice(Integer productId, Integer brandId, OffsetDateTime applicationDate);

}
