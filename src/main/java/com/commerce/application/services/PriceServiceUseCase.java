package com.commerce.application.services;

import com.commerce.application.exceptions.PriceException;
import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.entities.Price;
import com.commerce.domain.model.PriceConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * The type Price service use case.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PriceServiceUseCase implements PriceServicePort {

    private final PriceRepositoryPort priceRepositoryPort;

    @Override
    public Price getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        var prices = priceRepositoryPort.getPrices(productId, brandId, applicationDate);

        return prices.stream().max(Comparator.comparing(Price::getPriority)).orElseThrow(() ->
                new PriceException(PriceConstants.ERROR_NOT_FOUND,
                        PriceConstants.ERROR_CODE_NOT_FOUND));
    }
}
